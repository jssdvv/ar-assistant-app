package com.jssdvv.ara.machinery.domain.repository

import com.jssdvv.ara.machinery.domain.model.MachineEntity
import kotlinx.coroutines.flow.Flow

interface MachineRepository {
    fun getAllMachinesByNameAsc(): Flow<List<MachineEntity>>

    fun getAllMachinesByNameDesc(): Flow<List<MachineEntity>>

    fun getAllMachinesByCategoryAsc(): Flow<List<MachineEntity>>

    fun getAllMachinesByCategoryDesc(): Flow<List<MachineEntity>>

    fun getAllMachinesByTimestampAsc(): Flow<List<MachineEntity>>

    fun getAllMachinesByTimestampDesc(): Flow<List<MachineEntity>>

    suspend fun insertMachine(entity: MachineEntity)

    suspend fun updateMachine(entity: MachineEntity)

    suspend fun deleteMachine(entity: MachineEntity)
}