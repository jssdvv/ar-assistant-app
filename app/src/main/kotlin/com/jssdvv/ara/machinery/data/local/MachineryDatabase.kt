package com.jssdvv.ara.machinery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jssdvv.ara.core.data.utility.UriStringTypeConverter
import com.jssdvv.ara.machinery.domain.model.ActivityEntity
import com.jssdvv.ara.machinery.domain.model.MachineEntity

@Database(
    entities = [
        MachineEntity::class,
        ActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(UriStringTypeConverter::class)
abstract class MachineryDatabase : RoomDatabase() {
    abstract val machineDao: MachineDao
    abstract val activityDao: ActivityDao

    companion object {
        const val DATABASE_NAME = "machinesDatabase"
    }
}