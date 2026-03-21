package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.StepEntity
import com.jssdvv.ara.machines.domain.model.Step

fun StepEntity.toDomain() = Step(
    id = id,
    activityId = activityId,
    orderNumber = orderNumber,
    name = title,
    description = description,
    imageUri = imageUri
)

fun Step.toEntity() = StepEntity(
    id = id,
    activityId = activityId,
    orderNumber = orderNumber,
    title = name,
    description = description,
    imageUri = imageUri
)