package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.MarkerEntity
import com.jssdvv.ara.machines.domain.model.Marker
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion

fun MarkerEntity.toDomain() = Marker(
    id = id,
    machineId = machineId,
    index = index,
    sizeCentimeters = sizeCentimeters,
    imageUri = imageUri,
    calibrated = calibrated,
    originOffsetTransform = Transform(
        position = Position(originOffsetVx, originOffsetVy, originOffsetVz),
        quaternion = Quaternion(originOffsetQx, originOffsetQy, originOffsetQz, originOffsetQw)
    )
)

fun Marker.toEntity() = MarkerEntity(
    id = id,
    machineId = machineId,
    index = index,
    sizeCentimeters = sizeCentimeters,
    imageUri = imageUri,
    calibrated = calibrated,
    originOffsetVx = originOffsetTransform.position.x,
    originOffsetVy = originOffsetTransform.position.y,
    originOffsetVz = originOffsetTransform.position.z,
    originOffsetQx = originOffsetTransform.quaternion.x,
    originOffsetQy = originOffsetTransform.quaternion.y,
    originOffsetQz = originOffsetTransform.quaternion.z,
    originOffsetQw = originOffsetTransform.quaternion.w
)