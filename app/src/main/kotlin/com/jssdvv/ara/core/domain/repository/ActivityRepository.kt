package com.jssdvv.ara.core.domain.repository

import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.domain.model.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAllActivitiesForMachineIdAsc(machineId: Int): Flow<List<Activity>>

    suspend fun insertActivity(model: Activity)

    suspend fun updateActivity(model: Activity)

    suspend fun deleteActivity(model: Activity)
}