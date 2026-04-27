package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.PivotComposite
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion

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
    global = global,
    offsetTransform = Transform(
        position = Position(offsetVx, offsetVy, offsetVz),
        quaternion = Quaternion(offsetQx, offsetQy, offsetQz, offsetQw)
    ),
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
    global = global,
    offsetVy = offsetTransform.position.y,
    offsetVx = offsetTransform.position.x,
    offsetVz = offsetTransform.position.z,
    offsetQx = offsetTransform.quaternion.x,
    offsetQy = offsetTransform.quaternion.y,
    offsetQz = offsetTransform.quaternion.z,
    offsetQw = offsetTransform.quaternion.w,
)

fun OperationWithTargets.toDomain() = OperationTargets(
    operation = operation.toDomain(),
    pivots = pivotTargets.mapTo(mutableSetOf(), PivotComposite::toDomain)
)

fun OperationTargets.toComposite() = OperationWithTargets(
    operation = operation.toEntity(),
    pivotTargets = pivots.map { it.toComposite(operation.id) }
)