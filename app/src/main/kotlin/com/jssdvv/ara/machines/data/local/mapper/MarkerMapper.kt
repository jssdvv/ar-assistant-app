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
    originOffsetPosition = Position(originOffsetVx, originOffsetVy, originOffsetVz),
    originOffsetRotation = Quaternion(originOffsetQx, originOffsetQy, originOffsetQz, originOffsetQw)
)

fun Marker.toEntity() = MarkerEntity(
    id = id,
    machineId = machineId,
    index = index,
    sizeCentimeters = sizeCentimeters,
    imageUri = imageUri,
    calibrated = calibrated,
    originOffsetVx = originOffsetPosition.x,
    originOffsetVy = originOffsetPosition.y,
    originOffsetVz = originOffsetPosition.z,
    originOffsetQx = originOffsetRotation.x,
    originOffsetQy = originOffsetRotation.y,
    originOffsetQz = originOffsetRotation.z,
    originOffsetQw = originOffsetRotation.w
)