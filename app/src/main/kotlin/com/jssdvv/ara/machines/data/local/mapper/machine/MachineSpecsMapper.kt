package com.jssdvv.ara.machines.data.local.mapper.machine

import com.jssdvv.ara.machines.data.local.entity.machine.MachineSpecsEntity
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs

fun MachineSpecsEntity.toDomain() = MachineSpecs(
    specsId = id,
    machineId = machineId,
    serviceCapacity = serviceCapacity,
    speed = speed,
    lubricant = lubricant,
    powerSupply = powerSupply,
    weight = weight,
    height = height,
    length = length,
    width = width,
    jobDesc = jobDesc,
    hoursPerDay = hoursPerDay,
    roomTemp = roomTemp,
    additionalDesc = additionalDesc,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun MachineSpecs.toEntity() = MachineSpecsEntity(
    id = specsId,
    machineId = machineId,
    serviceCapacity = serviceCapacity,
    speed = speed,
    lubricant = lubricant,
    powerSupply = powerSupply,
    weight = weight,
    height = height,
    length = length,
    width = width,
    jobDesc = jobDesc,
    hoursPerDay = hoursPerDay,
    roomTemp = roomTemp,
    additionalDesc = additionalDesc,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)