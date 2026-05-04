package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import kotlinx.coroutines.flow.Flow

interface MachineRepository {
    suspend fun selectMachineAndDetailsByMachineId(machineId: Int): MachineDetails
    fun searchModelsOrdered(search: String, orderState: OrderState): Flow<List<Machine>>
    fun selectModelsOrdered(orderState: OrderState): Flow<List<Machine>>
    suspend fun upsertMachine(vararg model: Machine)
    suspend fun deleteMachine(vararg model: Machine)
    suspend fun upsertMachineSpecs(vararg model: MachineSpecs)
    suspend fun deleteMachineSpecs(vararg model: MachineSpecs)
}