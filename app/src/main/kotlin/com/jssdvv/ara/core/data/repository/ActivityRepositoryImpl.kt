package com.jssdvv.ara.core.data.repository

import com.jssdvv.ara.core.data.local.dao.ActivityDao
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.data.mapper.toDomain
import com.jssdvv.ara.core.data.mapper.toEntity
import com.jssdvv.ara.core.domain.model.Activity
import com.jssdvv.ara.core.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivityRepositoryImpl(
    private val dao: ActivityDao
) : ActivityRepository {
    override fun getAllActivitiesForMachineIdAsc(machineId: Int): Flow<List<Activity>> {
        return dao.getAllActivitiesForMachineIdAsc(machineId).map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun insertActivity(model: Activity) {
        return dao.insertActivity(model.toEntity())
    }

    override suspend fun updateActivity(model: Activity) {
        return dao.updateActivity(model.toEntity())
    }

    override suspend fun deleteActivity(model: Activity) {
        return dao.deleteActivity(model.toEntity())
    }
}