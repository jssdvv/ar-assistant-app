package com.jssdvv.ara.machinery.presentation

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.domain.utility.OrderType
import com.jssdvv.ara.machinery.domain.model.MachineEntity
import com.jssdvv.ara.machinery.domain.usecase.MachineUseCases
import com.jssdvv.ara.machinery.domain.utility.MachineOrderKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachineryViewModel @Inject constructor(
    private val useCases: MachineUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(MachineryUiState())
    val state = _state.asStateFlow()

    private var _lastDeletedMachine: MachineEntity? = null

    private var _getMachinesJob: Job? = null

    init {
        getMachines(MachineOrderKey.Name(OrderType.ASCENDING))
    }

    fun onEvent(event: MachineryEvent) {
        when (event) {
            is MachineryEvent.OrderMachines -> {
                if (state.value.machineOrderKey::class == event.orderKey::class &&
                    state.value.machineOrderKey.orderType == event.orderKey.orderType
                ) {
                    return
                } else {
                    _state.value = state.value.copy(
                        machineOrderKey = event.orderKey
                    )
                }
                getMachines(event.orderKey)
            }

            is MachineryEvent.DeleteMachine -> {
                viewModelScope.launch {
                    useCases.deleteMachine(event.entity)
                    _lastDeletedMachine = event.entity
                }
            }

            is MachineryEvent.RestoreMachine -> {
                viewModelScope.launch {
                    useCases.insertMachine(_lastDeletedMachine ?: return@launch)
                    _lastDeletedMachine = null
                }
            }

            is MachineryEvent.ToggleOrderSectionVisibility -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getMachines(orderKey: MachineOrderKey) {
        _getMachinesJob?.cancel()
        _getMachinesJob = useCases.getMachines(orderKey).onEach { machines ->
            _state.value = state.value.copy(
                machines = machines,
                machineOrderKey = orderKey
            )
        }.launchIn(viewModelScope)
    }
}

@Stable
data class MachineryUiState(
    val machines: List<MachineEntity> = emptyList(),
    val machineOrderKey: MachineOrderKey = MachineOrderKey.Name(orderType = OrderType.ASCENDING),
    val isOrderSectionVisible: Boolean = false
)

sealed class MachineryEvent {
    data class OrderMachines(val orderKey: MachineOrderKey) : MachineryEvent()
    data class DeleteMachine(val entity: MachineEntity) : MachineryEvent()
    data object RestoreMachine: MachineryEvent()
    data object ToggleOrderSectionVisibility: MachineryEvent()
}