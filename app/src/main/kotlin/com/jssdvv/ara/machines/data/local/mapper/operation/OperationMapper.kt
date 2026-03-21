package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.TargetRenderableComposite
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.TargetRenderable
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion

fun OperationEntity.toDomain() = Operation(
    id = id,
    stepId = stepId,
    orderNumber = orderNumber,
    title = title,
    type = type,
    duration = duration,
    delay = delay,
    initialTransform = Transform(
        position = Position(
            x = initialXVector,
            y = initialYVector,
            z = initialZVector
        ),
        quaternion = Quaternion(
            x = initialXQuaternion,
            y = initialYQuaternion,
            z = initialZQuaternion,
            w = initialWQuaternion
        )
    ),
    finalTransform = Transform(
        position = Position(
            x = finalXVector,
            y = finalYVector,
            z = finalZVector
        ),
        quaternion = Quaternion(
            x = finalXQuaternion,
            y = finalYQuaternion,
            z = finalZQuaternion,
            w = finalWQuaternion
        )
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
    initialXVector = initialTransform.position.x,
    initialYVector = initialTransform.position.y,
    initialZVector = initialTransform.position.z,
    initialXQuaternion = initialTransform.quaternion.x,
    initialYQuaternion = initialTransform.quaternion.y,
    initialZQuaternion = initialTransform.quaternion.z,
    initialWQuaternion = initialTransform.quaternion.w,
    finalXVector = finalTransform.position.x,
    finalYVector = finalTransform.position.y,
    finalZVector = finalTransform.position.z,
    finalXQuaternion = finalTransform.quaternion.x,
    finalYQuaternion = finalTransform.quaternion.y,
    finalZQuaternion = finalTransform.quaternion.z,
    finalWQuaternion = finalTransform.quaternion.w,
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
    targetRenderables = targets.map(TargetRenderableComposite::toDomain)
)

fun OperationTargets.toComposite() = OperationWithTargets(
    operation = operation.toEntity(),
    targets = targetRenderables.map(TargetRenderable::toComposite)
)