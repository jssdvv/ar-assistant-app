package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.RenderableTargetComposite
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.RenderableTarget
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

fun OperationEntity.toDomain() = Operation(
    id = id,
    stepId = stepId,
    order = order,
    title = title,
    type = type,
    delay = delay,
    duration = duration,
    axis = axis,
    pivot = Float3(pivotX, pivotY, pivotZ),
    alpha = alpha,
    pitch = pitch,
    turns = turns,
    isGlobal = isGlobal,
    offsetPosition = Position(offsetVx, offsetVy, offsetVz),
    offsetRotation = Quaternion(offsetQx, offsetQy, offsetQz, offsetQw)
)

fun Operation.toEntity() = OperationEntity(
    id = id,
    stepId = stepId,
    order = order,
    title = title,
    type = type,
    delay = delay,
    duration = duration,
    axis = axis,
    pivotX = pivot.x,
    pivotY = pivot.y,
    pivotZ = pivot.z,
    alpha = alpha,
    pitch = pitch,
    turns = turns,
    isGlobal = isGlobal,
    offsetVy = offsetPosition.y,
    offsetVx = offsetPosition.x,
    offsetVz = offsetPosition.z,
    offsetQx = offsetRotation.x,
    offsetQy = offsetRotation.y,
    offsetQz = offsetRotation.z,
    offsetQw = offsetRotation.w,
)

fun OperationWithTargets.toDomain() = OperationTargets(
    operation = operation.toDomain(),
    targets = targets.map(RenderableTargetComposite::toDomain)
)

fun OperationTargets.toComposite() = OperationWithTargets(
    operation = operation.toEntity(),
    targets = targets.map(RenderableTarget::toComposite)
)