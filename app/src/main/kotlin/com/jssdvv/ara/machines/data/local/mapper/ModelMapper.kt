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
    positionFromOrigin = Position(
        x = xOffsetVector,
        y = yOffsetVector,
        z = zOffsetVector
    ),
    rotationFromOrigin = Quaternion(
        x = xOffsetQuaternion,
        y = yOffsetQuaternion,
        z = zOffsetQuaternion,
        w = wOffsetQuaternion
    ),
)

fun Model.toEntity() = ModelEntity(
    id = id,
    machineId = machineId,
    name = name,
    glbUri = glbUri,
    calibrated = calibrated,
    xOffsetVector = positionFromOrigin.x,
    yOffsetVector = positionFromOrigin.y,
    zOffsetVector = positionFromOrigin.z,
    xOffsetQuaternion = rotationFromOrigin.x,
    yOffsetQuaternion = rotationFromOrigin.y,
    zOffsetQuaternion = rotationFromOrigin.z,
    wOffsetQuaternion = rotationFromOrigin.w
)