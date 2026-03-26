package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.TargetRenderableComposite
import com.jssdvv.ara.machines.domain.model.RenderableTarget

fun TargetRenderableComposite.toDomain() = RenderableTarget(
    operationId = operationId,
    modelId = modelId,
    renderableIndex = renderableIndex,
    renderableName = renderableName
)

fun RenderableTarget.toComposite() = TargetRenderableComposite(
    operationId = operationId,
    modelId = modelId,
    renderableIndex = renderableIndex,
    renderableName = renderableName
)