package com.jssdvv.ara.core.data.mapper

import com.jssdvv.ara.core.data.local.entity.ModelEntity
import com.jssdvv.ara.core.domain.model.Model

fun ModelEntity.toDomain(): Model {
    return Model(
        modelId = modelId,
        machineId = machineId,
        name = name,
        fileUri = fileUri,
        description = description,
        timestamp = timestamp,
        positionX = positionX,
        positionY = positionY,
        positionZ = positionZ,
        rotationX = rotationX,
        rotationY = rotationY,
        rotationZ = rotationZ
    )
}

fun Model.toEntity(): ModelEntity {
    return ModelEntity(
        modelId = modelId,
        machineId = machineId,
        name = name,
        fileUri = fileUri,
        description = description,
        timestamp = timestamp,
        positionX = positionX,
        positionY = positionY,
        positionZ = positionZ,
        rotationX = rotationX,
        rotationY = rotationY,
        rotationZ = rotationZ
    )
}