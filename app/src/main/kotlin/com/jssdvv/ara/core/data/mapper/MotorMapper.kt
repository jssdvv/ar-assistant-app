package com.jssdvv.ara.core.data.mapper

import com.jssdvv.ara.core.data.local.entity.MotorEntity
import com.jssdvv.ara.core.domain.model.Motor

fun MotorEntity.toDomain(): Motor {
    return Motor(
        motorId = motorId,
        machineId = machineId,
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        fabricator = fabricator,
        supplier = supplier,
        price = price,
        acquisitionDate = acquisitionDate,
        weight = weight,
        weightUnit = weightUnit,
        frequency = frequency,
        frequencyUnit = frequencyUnit,
        rpm = rpm,
        eff = eff,
        sf = sf,
        power = power,
        powerUnit = powerUnit,
        voltageMax = voltageMax,
        voltageMin = voltageMin,
        voltageUnit = voltageUnit,
        currentMax = currentMax,
        currentMin = currentMin,
        currentUnit = currentUnit,
        pf = pf,
        ph = ph,
        cosPhi = cosPhi,
        amb = amb,
        ambUnit = ambUnit,
        fr = fr,
        duty = duty
    )
}

fun Motor.toEntity(): MotorEntity {
    return MotorEntity(
        motorId = motorId,
        machineId = machineId,
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        fabricator = fabricator,
        supplier = supplier,
        price = price,
        acquisitionDate = acquisitionDate,
        weight = weight,
        weightUnit = weightUnit,
        frequency = frequency,
        frequencyUnit = frequencyUnit,
        rpm = rpm,
        eff = eff,
        sf = sf,
        power = power,
        powerUnit = powerUnit,
        voltageMax = voltageMax,
        voltageMin = voltageMin,
        voltageUnit = voltageUnit,
        currentMax = currentMax,
        currentMin = currentMin,
        currentUnit = currentUnit,
        pf = pf,
        ph = ph,
        cosPhi = cosPhi,
        amb = amb,
        ambUnit = ambUnit,
        fr = fr,
        duty = duty
    )
}