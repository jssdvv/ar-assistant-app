package com.jssdvv.ara.core.data.local.converter

import androidx.room.TypeConverter
import com.jssdvv.ara.machines.domain.model.activity.ActivityType
import com.jssdvv.ara.machines.domain.model.machine.MachineType

/**
 * Converts [Enum] objects to [Int] ordinals and vice versa for Room database storage.
 */
class EnumTypeConverter {

    @TypeConverter
    fun toMachineType(ordinal: Int): MachineType = enumValues<MachineType>()[ordinal]

    @TypeConverter
    fun fromMachineType(machineType: MachineType) = machineType.ordinal

    @TypeConverter
    fun toActivityType(ordinal: Int): ActivityType = enumValues<ActivityType>()[ordinal]

    @TypeConverter
    fun fromActivityType(activityType: ActivityType) = activityType.ordinal
}