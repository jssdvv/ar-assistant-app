package com.jssdvv.ara.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "modelTable",
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
data class ModelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "modelId")
    val modelId: Int = 0,

    @ColumnInfo(name = "machineId")
    val machineId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "fileUri")
    val fileUri: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "positionX")
    val positionX: Float,

    @ColumnInfo(name = "positionY")
    val positionY: Float,

    @ColumnInfo(name = "positionZ")
    val positionZ: Float,

    @ColumnInfo(name = "rotationX")
    val rotationX: Float,

    @ColumnInfo(name = "rotationY")
    val rotationY: Float,

    @ColumnInfo(name = "rotationZ")
    val rotationZ: Float,
)

class InvalidModelException(exception: String) : Exception(exception)