package com.jssdvv.ara.machinery.presentation.machinery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.domain.model.Machine
import com.jssdvv.ara.core.domain.utility.OrderType
import com.jssdvv.ara.machinery.domain.model.MachineUseCases
import com.jssdvv.ara.machinery.domain.utility.MachineOrderKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachineryViewModel @Inject constructor(
    private val useCases: MachineUseCases,
) : ViewModel() {

    init {
        getMachines(MachineOrderKey.Name(OrderType.ASCENDING))
    }

    private var _lastDeletedMachine: Machine? = null
    private var _getMachinesJob: Job? = null
    private val _orderKey = MutableStateFlow<MachineOrderKey>(MachineOrderKey.Name(OrderType.ASCENDING))
    private val _machineList = MutableStateFlow<List<Machine>>(emptyList())

    val uiState: StateFlow<MachineryUiState> = combine(
        _orderKey,
        _machineList,
    ) { orderKey, machineList ->
        MachineryUiState.Success(
            orderKey,
            machineList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MachineryUiState.Loading
    )

    private fun getMachines(orderKey: MachineOrderKey) {
        _getMachinesJob?.cancel()
        _getMachinesJob = useCases.getMachines(orderKey).onEach {
            _machineList.value = it
        }.launchIn(viewModelScope)
    }

    fun onUiEvent(event: MachineryUiEvent) {
        when (event) {
            is MachineryUiEvent.OrderMachines -> {
                _orderKey.value = event.orderKey
                getMachines(event.orderKey)
            }

            is MachineryUiEvent.DeleteMachine -> {
                viewModelScope.launch {
                    useCases.deleteMachine(event.model)
                    _lastDeletedMachine = event.model
                }
            }

            is MachineryUiEvent.RestoreMachine -> {
                viewModelScope.launch {
                    useCases.insertMachine(_lastDeletedMachine ?: return@launch)
                    _lastDeletedMachine = null
                }
            }
        }
    }
}

sealed class MachineryUiEvent {
    data class OrderMachines(val orderKey: MachineOrderKey) : MachineryUiEvent()
    data class DeleteMachine(val model: Machine) : MachineryUiEvent()
    data object RestoreMachine : MachineryUiEvent()
}

sealed interface MachineryUiState {

    data object Loading : MachineryUiState

    data class Success(
        val orderKey: MachineOrderKey,
        val machineryList: List<Machine>,
    ) : MachineryUiState
}