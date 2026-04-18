package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.ModelEntity
import com.jssdvv.ara.machines.domain.model.Model
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion

fun ModelEntity.toDomain() = Model(
    id = id,
    machineId = machineId,
    name = name,
    glbUri = glbUri,
    calibrated = calibrated,
    offsetTransform = Transform(
        position = Position(offsetVx, offsetVy, offsetVz),
        quaternion = Quaternion(offsetQx, offsetQy, offsetQz, offsetQw),
    )
)

fun Model.toEntity() = ModelEntity(
    id = id,
    machineId = machineId,
    name = name,
    glbUri = glbUri,
    calibrated = calibrated,
    offsetVx = offsetTransform.position.x,
    offsetVy = offsetTransform.position.y,
    offsetVz = offsetTransform.position.z,
    offsetQx = offsetTransform.quaternion.x,
    offsetQy = offsetTransform.quaternion.y,
    offsetQz = offsetTransform.quaternion.z,
    offsetQw = offsetTransform.quaternion.w
)