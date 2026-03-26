package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.TargetRenderableComposite
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
    orderNumber = orderNumber,
    title = title,
    type = type,
    duration = duration,
    delay = delay,
    offsetPosition = Position(
        x = offsetXVector,
        y = offsetYVector,
        z = offsetZVector
    ),
    offsetQuaternion = Quaternion(
        x = offsetXQuaternion,
        y = offsetYQuaternion,
        z = offsetZQuaternion,
        w = offsetWQuaternion
    ),
    screwPitch = screwPitch,
    axis = Float3(axisX, axisY, axisZ),
    pivot = Float3(pivotX, pivotY, pivotZ),
)

fun Operation.toEntity() = OperationEntity(
    id = id,
    stepId = stepId,
    orderNumber = orderNumber,
    title = title,
    type = type,
    duration = duration,
    delay = delay,
    offsetXVector = offsetPosition.x,
    offsetYVector = offsetPosition.y,
    offsetZVector = offsetPosition.z,
    offsetXQuaternion = offsetQuaternion.x,
    offsetYQuaternion = offsetQuaternion.y,
    offsetZQuaternion = offsetQuaternion.z,
    offsetWQuaternion = offsetQuaternion.w,
    screwPitch = screwPitch,
    axisX = axis.x,
    axisY = axis.y,
    axisZ = axis.z,
    pivotX = pivot.x,
    pivotY = pivot.y,
    pivotZ = pivot.z,
)

fun OperationWithTargets.toDomain() = OperationTargets(
    operation = operation.toDomain(),
    renderableTargets = targets.map(TargetRenderableComposite::toDomain)
)

fun OperationTargets.toComposite() = OperationWithTargets(
    operation = operation.toEntity(),
    targets = renderableTargets.map(RenderableTarget::toComposite)
)