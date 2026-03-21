package com.jssdvv.ara.core.data.local.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Converts [Date] objects to [Long] timestamps and vice versa for Room database storage.
 */
class DateTypeConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? = date?.time
}