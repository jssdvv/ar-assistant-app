package com.jssdvv.ara.core.data.local.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Converts [Date] objects to [Long] timestamps and vice versa for Room database storage.
 */
class DateTypeConverter {

    /**
     * Converts a [Long] timestamp to a [Date].
     *
     * @param [value] The timestamp in milliseconds.
     *
     * @return The corresponding [Date], or `null` if the value is `null`.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    /**
     * Converts a [Date] to a [Long] timestamp.
     *
     * @param [date] The [Date] to convert.
     *
     * @return The timestamp in milliseconds, or `null` if the date is `null`.
     */
    @TypeConverter
    fun toTimestamp(date: Date?): Long? = date?.time
}