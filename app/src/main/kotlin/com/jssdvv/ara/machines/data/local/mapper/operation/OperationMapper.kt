package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.RenderableTargetComposite
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.RenderableTarget
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
    turns = turns,
    isGlobal = isGlobal,
    containerOffsetPosition = Position(offsetVx, offsetVy, offsetVz),
    containerOffsetQuaternion = Quaternion(offsetQx, offsetQy, offsetQz, offsetQw)
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
    turns = turns,
    isGlobal = isGlobal,
    offsetVy = containerOffsetPosition.y,
    offsetVx = containerOffsetPosition.x,
    offsetVz = containerOffsetPosition.z,
    offsetQx = containerOffsetQuaternion.x,
    offsetQy = containerOffsetQuaternion.y,
    offsetQz = containerOffsetQuaternion.z,
    offsetQw = containerOffsetQuaternion.w,
)

fun OperationWithTargets.toDomain() = OperationTargets(
    operation = operation.toDomain(),
    targets = targets.map(RenderableTargetComposite::toDomain)
)

fun OperationTargets.toComposite() = OperationWithTargets(
    operation = operation.toEntity(),
    targets = targets.map(RenderableTarget::toComposite)
)