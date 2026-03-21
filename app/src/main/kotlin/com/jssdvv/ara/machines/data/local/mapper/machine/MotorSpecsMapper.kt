package com.jssdvv.ara.machines.data.local.mapper.machine

import com.jssdvv.ara.machines.data.local.entity.machine.MotorSpecsEntity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs

fun MotorSpecsEntity.toDomain() = MotorSpecs(
    id = id,
    machineId = machineId,
    effClass = effClass,
    phasesNumber = phasesNumber,
    nominalPower = nominalPower,
    frequency = frequency,
    rpm = rpm,
    rpmRange = rpmRange,
    nominalVoltage = nominalVoltage,
    nominalCurrent = nominalCurrent,
    serviceFactor = serviceFactor,
    powerFactor = powerFactor,
    duty = duty,
    roomTemp = roomTemp,
    energyEff = energyEff,
    maxAltitude = maxAltitude,
    ingressProtection = ingressProtection,
    mountingType = mountingType,
    frameType = frameType,
    coolingMethod = coolingMethod,
    driveEnd = driveEnd,
    nonDriveEnd = nonDriveEnd,
    insulationClass = insulationClass,
    insulationClassTemp = insulationClassTemp,
    weight = weight,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun MotorSpecs.toEntity() = MotorSpecsEntity(
    id = id,
    machineId = machineId,
    effClass = effClass,
    phasesNumber = phasesNumber,
    nominalPower = nominalPower,
    frequency = frequency,
    rpm = rpm,
    rpmRange = rpmRange,
    nominalVoltage = nominalVoltage,
    nominalCurrent = nominalCurrent,
    serviceFactor = serviceFactor,
    powerFactor = powerFactor,
    duty = duty,
    roomTemp = roomTemp,
    energyEff = energyEff,
    maxAltitude = maxAltitude,
    ingressProtection = ingressProtection,
    mountingType = mountingType,
    frameType = frameType,
    coolingMethod = coolingMethod,
    driveEnd = driveEnd,
    nonDriveEnd = nonDriveEnd,
    insulationClass = insulationClass,
    insulationClassTemp = insulationClassTemp,
    weight = weight,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)