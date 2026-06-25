package com.jssdvv.ara.machines.presentation.destination.machines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.usecase.SearchMachines
import com.jssdvv.ara.machines.domain.usecase.SelectMachines
import com.jssdvv.ara.machines.domain.usecase.UpsertMachines
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
    private val selectMachinesUseCase: SelectMachines,
    private val searchMachinesUseCase: SearchMachines,
    private val upsertMachinesUseCase: UpsertMachines,
) : ViewModel() {

    private val orderState = MutableStateFlow(OrderState())
    private val machines = MutableStateFlow(emptyList<Machine>())
    private val searchedMachines = MutableStateFlow(emptyList<Machine>())

    private var getMachinesJob: Job? = null
    private var searchMachinesJob: Job? = null

    init {
        getMachines(orderState.value)
    }

    val uiState: StateFlow<MachinesUiState> = combine(
        orderState,
        machines,
        searchedMachines,
        MachinesUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MachinesUiState.Loading
    )

    private fun getMachines(orderState: OrderState) {
        getMachinesJob?.cancel()
        getMachinesJob = selectMachinesUseCase(orderState).onEach { machines ->
            this.machines.value = machines
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: MachinesEvent) {
        when (event) {
            is MachinesEvent.OrderMachines -> onOrderMachines(event.orderState)
            is MachinesEvent.SearchMachines -> onSearchMachines(event.search)
            is MachinesEvent.CreateMachine -> createMachine(event.machine)
        }
    }

    private fun onOrderMachines(orderState: OrderState) {
        this.orderState.value = orderState
        getMachines(orderState)
    }

    private fun onSearchMachines(search: String) {
        searchMachinesJob?.cancel()
        searchMachinesJob = searchMachinesUseCase(search, orderState.value).onEach { machines ->
            searchedMachines.value = machines
        }.launchIn(viewModelScope)
    }

    private fun createMachine(machine: Machine) {

        val relation = MachineDetails(machine = machine)

        viewModelScope.launch {
            upsertMachinesUseCase(relation)
        }
    }
}

sealed class MachinesEvent {
    data class OrderMachines(val orderState: OrderState) : MachinesEvent()
    data class SearchMachines(val search: String) : MachinesEvent()
    data class CreateMachine(val machine: Machine) : MachinesEvent()
}

sealed interface MachinesUiState {
    data object Loading : MachinesUiState
    data class Success(
        val orderState: OrderState,
        val machines: List<Machine>,
        val searchedMachines: List<Machine>,
    ) : MachinesUiState
}