package com.jssdvv.ara.core.data.mapper

import com.jssdvv.ara.core.data.local.entity.FabricationDataEntity
import com.jssdvv.ara.core.domain.model.FabricationData

fun FabricationDataEntity.toDomain(): FabricationData {
    return FabricationData(
        fabricationDataId = fabricationDataId,
        machineId = machineId,
        inventoryNumber = inventoryNumber,
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        fabricator = fabricator,
        supplier = supplier,
        price = price,
        acquisitionDate = acquisitionDate,
        machineStatus = machineStatus,
        capacity = capacity,
        capacityUnit = capacityUnit,
        hoursPerDay = hoursPerDay,
        calibrationCertificate = calibrationCertificate
    )
}

fun FabricationData.toEntity(): FabricationDataEntity {
    return FabricationDataEntity(
        fabricationDataId = fabricationDataId,
        machineId = machineId,
        inventoryNumber = inventoryNumber,
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        fabricator = fabricator,
        supplier = supplier,
        price = price,
        acquisitionDate = acquisitionDate,
        machineStatus = machineStatus,
        capacity = capacity,
        capacityUnit = capacityUnit,
        hoursPerDay = hoursPerDay,
        calibrationCertificate = calibrationCertificate
    )
}