package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.data.mapper.toEntity
import com.jssdvv.ara.core.domain.model.Activity
import com.jssdvv.ara.core.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetActivities(
    private val repository: ActivityRepository
) {
    operator fun invoke(machineId: Int): Flow<List<Activity>> {
        return repository.getAllActivitiesForMachineIdAsc(machineId)
    }
}