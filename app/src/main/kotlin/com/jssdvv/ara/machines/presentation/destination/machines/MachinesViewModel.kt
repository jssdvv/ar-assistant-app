package com.jssdvv.ara.machines.presentation.destination.machines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.type.MachineOrderKey
import com.jssdvv.ara.machines.domain.usecase.SearchMachines
import com.jssdvv.ara.machines.domain.usecase.SelectMachines
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
class MachineryViewModel @Inject constructor(
    private val selectMachinesUseCase: SelectMachines,
    private val searchMachinesUseCase: SearchMachines
) : ViewModel() {

    private val orderKey = MutableStateFlow(MachineOrderKey.NAME)
    private val orderType = MutableStateFlow(OrderType.ASCENDING)
    private val machines = MutableStateFlow(emptyList<Machine>())
    private val searchedMachines = MutableStateFlow(emptyList<Machine>())

    private var getMachinesJob: Job? = null
    private var searchMachinesJob: Job? = null

    init {
        getMachines(orderType.value, orderKey.value)
    }

    val uiState: StateFlow<MachinesUiState> = combine(
        orderType,
        orderKey,
        machines,
        searchedMachines,
        MachinesUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MachinesUiState.Loading
    )

    private fun getMachines(orderType: OrderType, orderKey: MachineOrderKey) {
        getMachinesJob?.cancel()
        getMachinesJob = selectMachinesUseCase(orderKey, orderType).onEach { machines ->
            this.machines.value = machines
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: MachinesEvent) {
        when (event) {
            is MachinesEvent.OrderMachines -> onOrderMachines(event.orderKey, event.orderType)
            is MachinesEvent.SearchMachines -> onSearchMachines(event.search)
        }
    }

    private fun onOrderMachines(orderKey: MachineOrderKey, orderType: OrderType) {
        this.orderType.value = orderType
        this.orderKey.value = orderKey
        getMachines(orderType, orderKey)
    }

    private fun onSearchMachines(search: String) {
        searchMachinesJob?.cancel()
        searchMachinesJob = searchMachinesUseCase(
            search = search,
            orderKey = orderKey.value,
            orderType = orderType.value
        ).onEach { machines ->
            searchedMachines.value = machines
        }.launchIn(viewModelScope)
    }
}

sealed class MachinesEvent {
    data class OrderMachines(
        val orderKey: MachineOrderKey,
        val orderType: OrderType,
    ) : MachinesEvent()

    data class SearchMachines(val search: String) : MachinesEvent()
}

sealed interface MachinesUiState {
    data object Loading : MachinesUiState
    data class Success(
        val orderType: OrderType,
        val orderKey: MachineOrderKey,
        val machines: List<Machine>,
        val searchedMachines: List<Machine>,
    ) : MachinesUiState
}