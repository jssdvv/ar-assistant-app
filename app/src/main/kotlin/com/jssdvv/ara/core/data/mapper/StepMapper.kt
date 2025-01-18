package com.jssdvv.ara.core.data.mapper

import com.jssdvv.ara.core.data.local.entity.StepEntity
import com.jssdvv.ara.core.domain.model.Step

fun StepEntity.toDomain(): Step {
    return Step(
        stepId = stepId,
        activityId = activityId,
        name = name,
        orderNumber = orderNumber,
        description = description,
        models = models,
        tools = tools,
        parts = parts
    )
}

fun Step.toEntity(): StepEntity {
    return StepEntity(
        stepId = stepId,
        activityId = activityId,
        name = name,
        orderNumber = orderNumber,
        description = description,
        models = models,
        tools = tools,
        parts = parts
    )
}