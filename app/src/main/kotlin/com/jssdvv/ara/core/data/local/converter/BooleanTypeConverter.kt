package com.jssdvv.ara.core.data.local.converter

import androidx.room.TypeConverter

class BooleanTypeConverter {

    /**
     * Converts a [Int] value to a [Boolean].
     *
     * @param [integer] The value to convert.
     *
     * @return The corresponding [Boolean].
     */
    @TypeConverter
    fun fromInteger(integer: Int): Boolean = integer == 1

    /**
     * Converts a [Boolean] to a [Int] value.
     *
     * @param [boolean] The [Boolean] to convert.
     *
     * @return The corresponding [Int] value.
     */
    @TypeConverter
    fun toInteger(boolean: Boolean): Int = if (boolean) 1 else 0
}