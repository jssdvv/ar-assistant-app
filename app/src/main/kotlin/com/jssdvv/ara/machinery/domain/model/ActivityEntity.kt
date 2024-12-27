package com.jssdvv.ara.machinery.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activityTable",
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
        Index(value = ["machineId"])
    ]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activityId") val activityId: Int = 0,
    @ColumnInfo(name = "machineId") val machineId: Int,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "imageUri") val imageUri: Uri?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "frequency") val frequency: String?,
    @ColumnInfo(name = "frequencyUnit") val frequencyUnit: String?,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

class InvalidActivityException(exception: String) : Exception(exception)