package com.jssdvv.ara.machines.data.local.mapper.machine

import com.jssdvv.ara.machines.data.local.entity.machine.MotorIdentityEntity
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity

fun MotorIdentityEntity.toDomain() = MotorIdentity(
    id = id,
    machineId = machineId,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    productNumber = productNumber,
    fabricationCountry = fabricationCountry,
    standards = standards,
    fabricationYear = fabricationYear,
    price = price,
    acquisitionDate = acquisitionDate,
    imageUri = imageUri,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun MotorIdentity.toEntity() = MotorIdentityEntity(
    id = id,
    machineId = machineId,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    productNumber = productNumber,
    fabricationCountry = fabricationCountry,
    standards = standards,
    fabricationYear = fabricationYear,
    price = price,
    acquisitionDate = acquisitionDate,
    imageUri = imageUri,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)