package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.ActivityEntity
import com.jssdvv.ara.machines.domain.model.Activity

fun ActivityEntity.toDomain() = Activity(
    id = id,
    machineId = machineId,
    name = name,
    type = type,
    description = description,
    frequency = frequency,
    frequencyUnit = frequencyUnit,
    imageUri = imageUri,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun Activity.toEntity() = ActivityEntity(
    id = id,
    machineId = machineId,
    name = name,
    type = type,
    description = description,
    frequency = frequency,
    frequencyUnit = frequencyUnit,
    imageUri = imageUri,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)