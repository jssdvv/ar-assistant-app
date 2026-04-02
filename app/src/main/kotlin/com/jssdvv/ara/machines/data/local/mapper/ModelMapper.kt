package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.ModelEntity
import com.jssdvv.ara.machines.domain.model.Model
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

fun ModelEntity.toDomain() = Model(
    id = id,
    machineId = machineId,
    name = name,
    glbUri = glbUri,
    calibrated = calibrated,
    offsetPosition = Position(offsetVx, offsetVy, offsetVz),
    offsetRotation = Quaternion(offsetQx, offsetQy, offsetQz, offsetQw)
)

fun Model.toEntity() = ModelEntity(
    id = id,
    machineId = machineId,
    name = name,
    glbUri = glbUri,
    calibrated = calibrated,
    offsetVx = offsetPosition.x,
    offsetVy = offsetPosition.y,
    offsetVz = offsetPosition.z,
    offsetQx = offsetRotation.x,
    offsetQy = offsetRotation.y,
    offsetQz = offsetRotation.z,
    offsetQw = offsetRotation.w
)