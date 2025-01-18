package com.jssdvv.ara.core.domain.repository

import com.jssdvv.ara.core.data.local.entity.MachineEntity
import com.jssdvv.ara.core.domain.model.Machine
import kotlinx.coroutines.flow.Flow

interface MachineRepository {
    fun getAllMachinesByNameAsc(): Flow<List<Machine>>

    fun getAllMachinesByNameDesc(): Flow<List<Machine>>

    fun getAllMachinesByCategoryAsc(): Flow<List<Machine>>

    fun getAllMachinesByCategoryDesc(): Flow<List<Machine>>

    fun getAllMachinesByTimestampAsc(): Flow<List<Machine>>

    fun getAllMachinesByTimestampDesc(): Flow<List<Machine>>

    suspend fun insertMachine(model: Machine)

    suspend fun updateMachine(model: Machine)

    suspend fun deleteMachine(model: Machine)
}