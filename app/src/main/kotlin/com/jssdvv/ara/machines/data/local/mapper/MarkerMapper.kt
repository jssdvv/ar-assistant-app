package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.MarkerEntity
import com.jssdvv.ara.machines.domain.model.Marker
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

fun MarkerEntity.toDomain() = Marker(
    id = id,
    machineId = machineId,
    index = index,
    sizeCentimeters = sizeCentimeters,
    imageUri = imageUri,
    calibrated = calibrated,
    originPosition = Position(
        x = xOriginVector,
        y = yOriginVector,
        z = zOriginVector
    ),
    originRotation = Quaternion(
        x = xOriginQuaternion,
        y = yOriginQuaternion,
        z = zOriginQuaternion,
        w = wOriginQuaternion
    )
)

fun Marker.toEntity() = MarkerEntity(
    id = id,
    machineId = machineId,
    index = index,
    sizeCentimeters = sizeCentimeters,
    imageUri = imageUri,
    calibrated = calibrated,
    xOriginVector = originPosition.x,
    yOriginVector = originPosition.y,
    zOriginVector = originPosition.z,
    xOriginQuaternion = originRotation.x,
    yOriginQuaternion = originRotation.y,
    zOriginQuaternion = originRotation.z,
    wOriginQuaternion = originRotation.w
)