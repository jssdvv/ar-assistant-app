package com.jssdvv.ara.machinery.domain.usecase

data class ActivityUseCases(
    val getActivities: GetActivities,
    val insertActivity: InsertActivity,
    val updateActivity: UpdateActivity,
    val deleteActivity: DeleteActivity
)