package com.jssdvv.ara.machinery.domain.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "machineTable")
data class MachineEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "machineId") val machineId: Int = 0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "imageUri") val imageUri: Uri?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

class InvalidMachineException(exception: String) : Exception(exception)