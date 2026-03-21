package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

data class ActivitiesDataManager(
    val select: SelectActivities,
    val upsert: UpsertActivities,
    val delete: DeleteActivities,
)

class CountActivities(private val repository: ActivityRepository) {
    operator fun invoke(machineId: Int): Flow<Int> = repository.countActivitiesByMachineId(machineId)
}

class SelectActivities(private val repository: ActivityRepository) {
    operator fun invoke(machineId: Int): Flow<List<Activity>> =
        repository.selectActivitiesByMachineId(machineId)
}

class UpsertActivities(private val repository: ActivityRepository) {
    suspend operator fun invoke(vararg model: Activity) = repository.upsertActivity(*model)
}

class DeleteActivities(private val repository: ActivityRepository) {
    suspend operator fun invoke(vararg model: Activity) = repository.deleteActivity(*model)
}