package com.jssdvv.ara.core.data.mapper

import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.domain.model.Activity

fun ActivityEntity.toDomain(): Activity {
    return Activity(
        activityId = activityId,
        machineId = machineId,
        category = category,
        name = name,
        imageUri = imageUri,
        description = description,
        frequency = frequency,
        frequencyUnit = frequencyUnit,
        timestamp = timestamp
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        activityId = activityId,
        machineId = machineId,
        category = category,
        name = name,
        imageUri = imageUri,
        description = description,
        frequency = frequency,
        frequencyUnit = frequencyUnit,
        timestamp = timestamp
    )
}