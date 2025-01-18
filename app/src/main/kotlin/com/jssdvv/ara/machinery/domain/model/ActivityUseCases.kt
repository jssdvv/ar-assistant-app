package com.jssdvv.ara.machinery.domain.model

import com.jssdvv.ara.machinery.domain.usecase.DeleteActivity
import com.jssdvv.ara.machinery.domain.usecase.GetActivities
import com.jssdvv.ara.machinery.domain.usecase.InsertActivity
import com.jssdvv.ara.machinery.domain.usecase.UpdateActivity

data class ActivityUseCases(
    val getActivities: GetActivities,
    val insertActivity: InsertActivity,
    val updateActivity: UpdateActivity,
    val deleteActivity: DeleteActivity
)