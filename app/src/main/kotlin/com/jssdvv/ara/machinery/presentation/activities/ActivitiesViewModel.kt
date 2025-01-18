package com.jssdvv.ara.machinery.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.domain.model.Activity
import com.jssdvv.ara.machinery.domain.model.ActivityUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val useCases: ActivityUseCases,
) : ViewModel() {

    private val machineId = MutableStateFlow<Int?>(null)
    private val activityList = MutableStateFlow<List<Activity>>(emptyList())
    private var _getActivitiesJob: Job? = null

    init {
        machineId.value?.let { machineId -> getActivities(machineId) }
    }

    val uiState: StateFlow<ActivitiesUiState> = combine(
        machineId,
        activityList
    ) { machineId, activityList ->
            ActivitiesUiState.Success(
                machineId,
                activityList
            )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ActivitiesUiState.Loading
    )

    fun onEvent(event: ActivitiesUiEvent) {
        when (event) {
            is ActivitiesUiEvent.GetMachineId -> {
                machineId.value = event.machineId
                getActivities(event.machineId)
            }

            else -> {}
        }
    }

    private fun getActivities(machineId: Int) {
        _getActivitiesJob?.cancel()
        _getActivitiesJob = useCases.getActivities(machineId).onEach { activities ->
            activityList.value = activities
        }.launchIn(viewModelScope)
    }
}

sealed class ActivitiesUiEvent {
    data class GetMachineId(val machineId: Int) : ActivitiesUiEvent()
    data class InsertActivity(val entity: ActivityEntity) : ActivitiesUiEvent()
    data class UpdateActivity(val entity: ActivityEntity) : ActivitiesUiEvent()
    data class DeleteActivity(val entity: ActivityEntity) : ActivitiesUiEvent()
    data object RestoreActivity : ActivitiesUiEvent()
}

sealed interface ActivitiesUiState {

    data object Loading : ActivitiesUiState

    data class Success(
        val machineId: Int?,
        val activities: List<Activity>,
    ) : ActivitiesUiState
}

