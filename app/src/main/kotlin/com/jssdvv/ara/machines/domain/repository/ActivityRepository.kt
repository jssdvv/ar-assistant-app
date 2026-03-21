package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.machines.domain.model.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun countActivitiesByMachineId(machineId: Int): Flow<Int>
    fun selectActivitiesByMachineId(machineId: Int): Flow<List<Activity>>
    suspend fun upsertActivity(vararg model: Activity)
    suspend fun deleteActivity(vararg model: Activity)
}