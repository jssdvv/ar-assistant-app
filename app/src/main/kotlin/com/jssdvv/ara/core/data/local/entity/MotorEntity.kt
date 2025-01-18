package com.jssdvv.ara.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "motorTable",
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
data class MotorEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "motorId")
    val motorId: Int = 0,

    @ColumnInfo(name = "machineId")
    val machineId: Int,

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

    @ColumnInfo(name = "weight")
    val weight: Float?,

    @ColumnInfo(name = "weightUnit")
    val weightUnit: String?,

    @ColumnInfo(name = "frequency")
    val frequency: Float?,

    @ColumnInfo(name = "frequencyUnit")
    val frequencyUnit: String?,

    @ColumnInfo(name = "rpm")
    val rpm: Int?,

    @ColumnInfo(name = "eff")
    val eff: Float?,

    @ColumnInfo(name = "sf")
    val sf: Float?,

    @ColumnInfo(name = "power")
    val power: Float?,

    @ColumnInfo(name = "powerUnit")
    val powerUnit: String?,

    @ColumnInfo(name = "voltageMax")
    val voltageMax: Float?,

    @ColumnInfo(name = "voltageMin")
    val voltageMin: Float?,

    @ColumnInfo(name = "voltageUnit")
    val voltageUnit: String?,

    @ColumnInfo(name = "currentMax")
    val currentMax: Float?,

    @ColumnInfo(name = "currentMin")
    val currentMin: Float?,

    @ColumnInfo(name = "currentUnit")
    val currentUnit: String?,

    @ColumnInfo(name = "pf")
    val pf: Float?,

    @ColumnInfo(name = "ph")
    val ph: Int?,

    @ColumnInfo(name = "cosPhi")
    val cosPhi: Float?,

    @ColumnInfo(name = "amb")
    val amb: Float?,

    @ColumnInfo(name = "ambUnit")
    val ambUnit: String?,

    @ColumnInfo(name = "fr")
    val fr: String?,

    @ColumnInfo(name = "duty")
    val duty: String?,
)

class InvalidMotorException(exception: String) : Exception(exception)