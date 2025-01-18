package com.jssdvv.ara.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityWithSteps(
    @Embedded val activity: ActivityEntity,
    @Relation(
        parentColumn = "activityId",
        entityColumn = "activityId"
    )
    val stepList: List<StepEntity>
)