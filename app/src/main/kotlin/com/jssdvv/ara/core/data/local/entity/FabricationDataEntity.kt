package com.jssdvv.ara.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fabricationDataTable",
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = ["machineId"],
            childColumns = ["machineId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = ["machineId"]
        )
    ]
)
data class FabricationDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fabricationDataId")
    val fabricationDataId: Int = 0,

    @ColumnInfo(name = "machineId")
    val machineId: Int,

    @ColumnInfo(name = "inventoryNumber")
    val inventoryNumber: Int?,

    @ColumnInfo(name = "brand")
    val brand: String?,

    @ColumnInfo(name = "model")
    val model: String?,

    @ColumnInfo(name = "serialNumber")
    val serialNumber: String?,

    @ColumnInfo(name = "fabricator")
    val fabricator: String?,

    @ColumnInfo(name = "supplier")
    val supplier: String?,

    @ColumnInfo(name = "price")
    val price: Float?,

    @ColumnInfo(name = "acquisitionDate")
    val acquisitionDate: Int?,

    @ColumnInfo(name = "machineStatus")
    val machineStatus: String?,

    @ColumnInfo(name = "capacity")
    val capacity: String?,

    @ColumnInfo(name = "capacityUnit")
    val capacityUnit: String?,

    @ColumnInfo(name = "hoursPerDay")
    val hoursPerDay: String?,

    @ColumnInfo(name = "calibrationCertificate")
    val calibrationCertificate: String?,
)

class InvalidFabricationDataException(exception: String) : Exception(exception)