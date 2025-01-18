package com.jssdvv.ara.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jssdvv.ara.core.data.local.converter.UriStringTypeConverter
import com.jssdvv.ara.core.data.local.dao.ActivityDao
import com.jssdvv.ara.core.data.local.dao.MachineDao
import com.jssdvv.ara.core.data.local.dao.ModelDao
import com.jssdvv.ara.core.data.local.dao.StepDao
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.data.local.entity.AnimationEntity
import com.jssdvv.ara.core.data.local.entity.FabricationDataEntity
import com.jssdvv.ara.core.data.local.entity.MachineEntity
import com.jssdvv.ara.core.data.local.entity.ModelEntity
import com.jssdvv.ara.core.data.local.entity.MotorEntity
import com.jssdvv.ara.core.data.local.entity.StepEntity

@Database(
    entities = [
        MachineEntity::class,
        ActivityEntity::class,
        StepEntity::class,
        ModelEntity::class,
        MotorEntity::class,
        FabricationDataEntity::class,
        AnimationEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    UriStringTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract val machineDao: MachineDao
    abstract val activityDao: ActivityDao
    abstract val modelDao: ModelDao
    abstract val stepDao: StepDao

    companion object {
        const val DATABASE_NAME = "appDatabase"
    }
}