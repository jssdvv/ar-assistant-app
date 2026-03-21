package com.jssdvv.ara.core.data.local.converter

import androidx.room.TypeConverter

class BooleanTypeConverter {

    @TypeConverter
    fun fromInteger(integer: Int): Boolean = integer == 1

    @TypeConverter
    fun toInteger(boolean: Boolean): Int = if (boolean) 1 else 0
}