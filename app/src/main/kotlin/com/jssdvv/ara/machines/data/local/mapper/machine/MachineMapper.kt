package com.jssdvv.ara.machines.data.local.mapper.machine

import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity
import com.jssdvv.ara.machines.data.local.relation.MachineAndDetails
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails

fun MachineEntity.toDomain() = Machine(
    id = id,
    code = code,
    name = name,
    type = type,
    location = location,
    brand = brand,
    model = model,
    serial = serial,
    fabricationYear = fabricationYear,
    price = price,
    acquisitionDate = acquisitionDate,
    imageUri = imageUri,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun Machine.toEntity() = MachineEntity(
    id = id,
    code = code,
    name = name,
    type = type,
    location = location,
    brand = brand,
    model = model,
    serial = serial,
    fabricationYear = fabricationYear,
    price = price,
    acquisitionDate = acquisitionDate,
    imageUri = imageUri,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun MachineAndDetails.toDomain() = MachineDetails(
    machine = machine.toDomain(),
    machineSpecs = machineSpecs.toDomain(),
    motorIdentity = motorIdentity.toDomain(),
    motorSpecs = motorSpecs.toDomain()
)

fun MachineDetails.toEntity() = MachineAndDetails(
    machine = machine.toEntity(),
    machineSpecs = machineSpecs.toEntity(),
    motorIdentity = motorIdentity.toEntity(),
    motorSpecs = motorSpecs.toEntity()
)