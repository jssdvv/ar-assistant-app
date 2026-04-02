package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.StepEntity
import com.jssdvv.ara.machines.domain.model.Step

fun StepEntity.toDomain() = Step(
    id = id,
    activityId = activityId,
    order = order,
    name = title,
    description = description,
    imageUri = imageUri
)

fun Step.toEntity() = StepEntity(
    id = id,
    activityId = activityId,
    order = order,
    title = name,
    description = description,
    imageUri = imageUri
)