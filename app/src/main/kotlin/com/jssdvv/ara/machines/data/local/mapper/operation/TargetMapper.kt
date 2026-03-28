package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.RenderableTargetComposite
import com.jssdvv.ara.machines.domain.model.RenderableTarget

fun RenderableTargetComposite.toDomain() = RenderableTarget(
    operationId = operationId,
    modelId = modelId,
    xxh3 = xxh3,
    name = name
)

fun RenderableTarget.toComposite() = RenderableTargetComposite(
    operationId = operationId,
    modelId = modelId,
    xxh3 = xxh3,
    name = name
)