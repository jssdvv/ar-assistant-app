package com.jssdvv.ara.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jssdvv.ara.core.data.local.converter.BooleanTypeConverter
import com.jssdvv.ara.core.data.local.converter.DateTypeConverter
import com.jssdvv.ara.core.data.local.converter.EnumTypeConverter
import com.jssdvv.ara.core.data.local.converter.UriTypeConverter
import com.jssdvv.ara.machines.data.local.dao.ActivityDao
import com.jssdvv.ara.machines.data.local.dao.DocumentDao
import com.jssdvv.ara.machines.data.local.dao.MachineDao
import com.jssdvv.ara.machines.data.local.dao.MarkerDao
import com.jssdvv.ara.machines.data.local.dao.ModelDao
import com.jssdvv.ara.machines.data.local.dao.MotorDao
import com.jssdvv.ara.machines.data.local.dao.OperationDao
import com.jssdvv.ara.machines.data.local.dao.StepDao
import com.jssdvv.ara.machines.data.local.entity.ActivityEntity
import com.jssdvv.ara.machines.data.local.entity.DocumentEntity
import com.jssdvv.ara.machines.data.local.entity.MarkerEntity
import com.jssdvv.ara.machines.data.local.entity.ModelEntity
import com.jssdvv.ara.machines.data.local.entity.StepEntity
import com.jssdvv.ara.machines.data.local.entity.ToolEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MachineSpecsEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MotorIdentityEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MotorSpecsEntity
import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.PivotComposite


@Database(
    entities = [
        // Technical Data Entities
        MachineEntity::class,
        MachineSpecsEntity::class,
        MotorIdentityEntity::class,
        MotorSpecsEntity::class,
        DocumentEntity::class,

        // Procedures Entities
        ToolEntity::class,
        ActivityEntity::class,
        StepEntity::class,
        OperationEntity::class,
        PivotComposite::class,

        // Augmented Entities
        ModelEntity::class,
        MarkerEntity::class,
    ],
    version = 1
)
@TypeConverters(
    UriTypeConverter::class,
    DateTypeConverter::class,
    EnumTypeConverter::class,
    BooleanTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "appDatabase"
        const val DATABASE_ASSET_PATH = "database/database.db"
    }

    abstract val machineDao: MachineDao
    abstract val activityDao: ActivityDao
    abstract val motorDao: MotorDao
    abstract val markerDao: MarkerDao
    abstract val modelDao: ModelDao
    abstract val documentDao: DocumentDao
    abstract val stepDao: StepDao
    abstract val operationDao: OperationDao
}