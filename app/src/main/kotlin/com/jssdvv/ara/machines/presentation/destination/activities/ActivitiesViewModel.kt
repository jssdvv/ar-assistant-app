package com.jssdvv.ara.machines.presentation.destination.activities

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.usecase.CountMarkers
import com.jssdvv.ara.machines.domain.usecase.SelectActivities
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val selectActivitiesUseCase: SelectActivities,
    private val countMarkersUseCase: CountMarkers,
) : ViewModel() {

    val machineId = savedStateHandle.toRoute<MachinesGraph.ActivitiesRoute>().machineId

    private val activities = MutableStateFlow<List<Activity>>(emptyList())
    private val counters = MutableStateFlow(ActivitiesMiniButtonsCounters())
    private var getActivitiesJob: Job? = null

    init {
        getActivities(machineId)
        getCounters(machineId)
    }

    val uiState: StateFlow<ActivitiesUiState> = combine(
        activities,
        counters,
        ActivitiesUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ActivitiesUiState.Loading
    )

    fun onEvent(event: ActivitiesEvent) {
        when (event) {
            else -> {}
        }
    }

    private fun getActivities(machineId: Int) {
        getActivitiesJob?.cancel()
        getActivitiesJob = selectActivitiesUseCase(machineId).onEach { activities ->
            this@ActivitiesViewModel.activities.value = activities
        }.launchIn(viewModelScope)
    }

    private fun getCounters(machineId: Int) {
        countMarkersUseCase(machineId).onEach { count ->
            counters.value = counters.value.copy(markersCount = count)
        }.launchIn(viewModelScope)

        countMarkersUseCase.countCalibratedMarkersByMachineId(machineId).onEach { count ->
            counters.value = counters.value.copy(markersCalibratedCount = count)
        }.launchIn(viewModelScope)
    }
}

sealed class ActivitiesEvent

sealed interface ActivitiesUiState {

    data object Loading : ActivitiesUiState

    data class Success(
        val activities: List<Activity>,
        val counters: ActivitiesMiniButtonsCounters,
    ) : ActivitiesUiState
}

data class ActivitiesMiniButtonsCounters(
    val markersCount: Int = 0,
    val markersCalibratedCount: Int = 0,
)