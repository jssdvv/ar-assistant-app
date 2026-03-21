package com.jssdvv.ara.core.data.local.converter

import androidx.room.TypeConverter
import com.jssdvv.ara.machines.domain.type.ActivityType
import com.jssdvv.ara.machines.domain.type.MachineType
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.domain.type.ToolType

/**
 * Converts [Enum] objects to [Int] ordinals and vice versa for Room database storage.
 */
class EnumTypeConverter {

    @TypeConverter
    fun toMachineType(ordinal: Int): MachineType = enumValues<MachineType>()[ordinal]

    @TypeConverter
    fun fromMachineType(type: MachineType) = type.ordinal

    @TypeConverter
    fun toActivityType(ordinal: Int): ActivityType = enumValues<ActivityType>()[ordinal]

    @TypeConverter
    fun fromActivityType(type: ActivityType) = type.ordinal

    @TypeConverter
    fun toOperationType(ordinal: Int): OperationType = enumValues<OperationType>()[ordinal]

    @TypeConverter
    fun fromOperationType(type: OperationType) = type.ordinal

    @TypeConverter
    fun toToolType(ordinal: Int): ToolType = enumValues<ToolType>()[ordinal]

    @TypeConverter
    fun fromToolType(type: ToolType) = type.ordinal
}