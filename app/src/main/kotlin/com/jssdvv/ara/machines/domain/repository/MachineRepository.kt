package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.core.domain.type.OrderKey
import kotlinx.coroutines.flow.Flow

interface MachineRepository {
    suspend fun selectMachineAndDetailsByMachineId(machineId: Int): MachineDetails

    fun searchModelsOrdered(
        search: String,
        orderKey: OrderKey,
        orderType: OrderType,
    ): Flow<List<Machine>>

    fun selectModelsOrdered(
        orderKey: OrderKey,
        orderType: OrderType
    ): Flow<List<Machine>>

    suspend fun upsertMachine(vararg model: Machine)
    suspend fun deleteMachine(vararg model: Machine)
    suspend fun upsertMachineSpecs(vararg model: MachineSpecs)
    suspend fun deleteMachineSpecs(vararg model: MachineSpecs)
}