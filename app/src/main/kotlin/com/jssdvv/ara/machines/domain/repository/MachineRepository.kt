package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.machines.data.local.relation.MachineWithActivities
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineActivities
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import kotlinx.coroutines.flow.Flow

interface MachineRepository {
    suspend fun selectMachineAndDetailsByMachineId(machineId: Int): MachineDetails
    fun selectMachineWithActivities():Flow<List<MachineActivities>>
    suspend fun getMachineIdByActivityId(activityId: Int): Int
    fun searchModelsOrdered(search: String, orderState: OrderState): Flow<List<Machine>>
    fun selectModelsOrdered(orderState: OrderState): Flow<List<Machine>>
    suspend fun upsertMachine(vararg model: Machine)
    suspend fun deleteMachine(vararg model: Machine)
    suspend fun upsertMachineSpecs(vararg model: MachineSpecs)
    suspend fun deleteMachineSpecs(vararg model: MachineSpecs)
    suspend fun upsertMotorSpecs(vararg model: MotorSpecs)
    suspend fun deleteMotorSpecs(vararg model: MotorSpecs)
    suspend fun upsertMotorIdentity(vararg model: MotorIdentity)
    suspend fun deleteMotorIdentity(vararg model: MotorIdentity)
    suspend fun upsertMachineAndDetails(relation: MachineDetails)
}