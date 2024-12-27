package com.jssdvv.ara.machinery.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.machinery.domain.model.ActivityEntity
import com.jssdvv.ara.machinery.domain.usecase.ActivityUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ActivitiesListViewModel @Inject constructor(
    private val useCases: ActivityUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(ActivitiesListUiState())
    val state = _state.asStateFlow()

    private var _getActivitiesJob: Job? = null

    init {
        _state.value.machineId?.let { getActivities(it) }
    }

    fun onEvent(event: ActivitiesListEvent) {
        when (event) {
            is ActivitiesListEvent.GetMachineId -> {
                if (state.value.machineId != event.machineId) {
                    _state.value = state.value.copy(
                        machineId = event.machineId
                    )
                }
                getActivities(event.machineId)
            }

            else -> {}
        }
    }

    private fun getActivities(machineId: Int) {
        _getActivitiesJob?.cancel()
        _getActivitiesJob = useCases.getActivities(machineId).onEach { activities ->
            _state.value = state.value.copy(
                activities = activities,
                machineId = machineId
            )
        }.launchIn(viewModelScope)
    }
}

data class ActivitiesListUiState(
    val activities: List<ActivityEntity> = emptyList(),
    val machineId: Int? = null
)

sealed class ActivitiesListEvent {
    data class GetMachineId(val machineId: Int): ActivitiesListEvent()
    data class InsertActivity(val entity: ActivityEntity) : ActivitiesListEvent()
    data class UpdateActivity(val entity: ActivityEntity) : ActivitiesListEvent()
    data class DeleteActivity(val entity: ActivityEntity) : ActivitiesListEvent()
    data object RestoreActivity: ActivitiesListEvent()
}