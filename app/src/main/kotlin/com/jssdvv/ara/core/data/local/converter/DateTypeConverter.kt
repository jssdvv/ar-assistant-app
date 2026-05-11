package com.jssdvv.ara.core.data.local.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

/**
 * Converts [Instant] objects to [Long] timestamps and vice versa for Room database storage.
 */
class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun toTimestamp(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)
}