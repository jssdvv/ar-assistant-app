package com.jssdvv.ara.machines.data.repository

import com.jssdvv.ara.machines.data.local.dao.ActivityDao
import com.jssdvv.ara.machines.data.local.entity.ActivityEntity
import com.jssdvv.ara.machines.data.local.mapper.toDomain
import com.jssdvv.ara.machines.data.local.mapper.toEntity
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivityRepositoryImpl(
    private val dao: ActivityDao,
) : ActivityRepository {

    override fun countActivitiesByMachineId(machineId: Int): Flow<Int> =
        dao.countActivitiesByMachineId(machineId)

    override fun selectActivitiesByMachineId(machineId: Int): Flow<List<Activity>> =
        dao.selectActivitiesByMachineId(machineId).map { it.map(ActivityEntity::toDomain) }

    override suspend fun upsertActivity(vararg model: Activity) =
        dao.upsertActivity(*model.map(Activity::toEntity).toTypedArray())

    override suspend fun deleteActivity(vararg model: Activity) =
        dao.deleteActivity(*model.map(Activity::toEntity).toTypedArray())
}