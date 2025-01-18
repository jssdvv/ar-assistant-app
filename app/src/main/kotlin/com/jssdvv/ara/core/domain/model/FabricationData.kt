package com.jssdvv.ara.core.domain.model

data class FabricationData(
    val fabricationDataId: Int,
    val machineId: Int,
    val inventoryNumber: Int?,
    val brand: String?,
    val model: String?,
    val serialNumber: String?,
    val fabricator: String?,
    val supplier: String?,
    val price: Float?,
    val acquisitionDate: Int?,
    val machineStatus: String?,
    val capacity: String?,
    val capacityUnit: String?,
    val hoursPerDay: String?,
    val calibrationCertificate: String?,
)