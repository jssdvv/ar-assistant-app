package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.domain.repository.MachineRepository
import kotlinx.coroutines.flow.Flow

data class MachinesDataManager(
    val select: SelectMachines,
    val upsert: UpsertMachines,
    val delete: DeleteMachines,
)

class SearchMachines(
    private val repository: MachineRepository,
) {
    operator fun invoke(search: String, orderState: OrderState): Flow<List<Machine>> =
        repository.searchModelsOrdered(search, orderState)
}

class SelectMachineAndDetails(private val repository: MachineRepository) {
    suspend operator fun invoke(machineId: Int): MachineDetails =
        repository.selectMachineAndDetailsByMachineId(machineId)
}

class SelectMachines(private val repository: MachineRepository) {
    operator fun invoke(orderState: OrderState): Flow<List<Machine>> =
        repository.selectModelsOrdered(orderState)
}

class UpsertMachines(private val repository: MachineRepository) {
    suspend operator fun invoke(vararg model: Machine) = repository.upsertMachine(*model)
}

class DeleteMachines(private val repository: MachineRepository) {
    suspend operator fun invoke(vararg model: Machine) = repository.deleteMachine(*model)
}

class UpsertMachineSpecs(private val repository: MachineRepository) {
    suspend operator fun invoke(vararg model: MachineSpecs) = repository.upsertMachineSpecs(*model)
}