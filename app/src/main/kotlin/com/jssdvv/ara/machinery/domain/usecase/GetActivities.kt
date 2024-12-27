package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.machinery.domain.model.ActivityEntity
import com.jssdvv.ara.machinery.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class GetActivities(
    private val repository: ActivityRepository
) {
    operator fun invoke(machineId: Int): Flow<List<ActivityEntity>> {
        return repository.getAllActivitiesForMachineIdAsc(machineId)
    }
}