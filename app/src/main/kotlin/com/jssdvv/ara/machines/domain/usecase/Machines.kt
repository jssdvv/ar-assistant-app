package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
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

class SelectMachines(private val repository: MachineRepository) {
    operator fun invoke(orderState: OrderState): Flow<List<Machine>> =
        repository.selectModelsOrdered(orderState)

    suspend fun selectMachineAndDetails(machineId: Int) : MachineDetails =
        repository.selectMachineAndDetailsByMachineId(machineId)
}

class UpsertMachines(private val repository: MachineRepository) {
    suspend operator fun invoke(vararg model: Machine) =
        repository.upsertMachine(*model)

    suspend operator fun invoke(vararg model: MachineSpecs) =
        repository.upsertMachineSpecs(*model)

    suspend operator fun invoke(vararg model: MotorSpecs) =
        repository.upsertMotorSpecs(*model)

    suspend operator fun invoke(vararg model: MotorIdentity) =
        repository.upsertMotorIdentity(*model)

    suspend operator fun invoke(relation: MachineDetails) =
        repository.upsertMachineAndDetails(relation)
}

class DeleteMachines(private val repository: MachineRepository) {
    suspend operator fun invoke(vararg model: Machine) = repository.deleteMachine(*model)
}