package com.jssdvv.ara.inventory.domain.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "inventoryItemsTable")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "imageUri") val imageUri: Uri?,
    @ColumnInfo(name = "brand") val brand: String?,
    @ColumnInfo(name = "suppliers") val suppliers: String?,
    @ColumnInfo(name = "quantity") val quantity: Float,
    @ColumnInfo(name = "storageUnit") val storageUnit: String,
    @ColumnInfo(name = "unitCost") val unitCost: Float,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

class InvalidInventoryItemException(exception: String) : Exception(exception)
