package com.jssdvv.ara.machinery.domain.repository

import com.jssdvv.ara.machinery.domain.model.ActivityEntity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAllActivitiesForMachineIdAsc(machineId: Int): Flow<List<ActivityEntity>>

    suspend fun insertActivity(entity: ActivityEntity)

    suspend fun updateActivity(entity: ActivityEntity)

    suspend fun deleteActivity(entity: ActivityEntity)
}