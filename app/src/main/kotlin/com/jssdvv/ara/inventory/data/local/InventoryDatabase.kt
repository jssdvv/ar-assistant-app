package com.jssdvv.ara.inventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jssdvv.ara.core.data.local.converter.UriStringTypeConverter
import com.jssdvv.ara.inventory.domain.models.InventoryItemEntity

@Database(entities = [InventoryItemEntity::class], version = 1, exportSchema = false)
@TypeConverters(UriStringTypeConverter::class)
abstract class InventoryDatabase : RoomDatabase() {
    abstract val inventoryItemDao: InventoryItemDao

    companion object {
        const val DATABASE_NAME = "inventoryDatabase"
    }
}
