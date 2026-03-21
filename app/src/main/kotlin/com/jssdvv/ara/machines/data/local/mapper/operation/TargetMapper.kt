package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.TargetRenderableComposite
import com.jssdvv.ara.machines.domain.model.TargetRenderable

fun TargetRenderableComposite.toDomain() = TargetRenderable(
    operationId = operationId,
    modelId = modelId,
    renderableIndex = renderableIndex,
    renderableName = renderableName
)

fun TargetRenderable.toComposite() = TargetRenderableComposite(
    operationId = operationId,
    modelId = modelId,
    renderableIndex = renderableIndex,
    renderableName = renderableName
)