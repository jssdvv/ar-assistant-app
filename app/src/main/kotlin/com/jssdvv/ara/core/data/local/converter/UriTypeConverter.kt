package com.jssdvv.ara.core.data.local.converter

import android.net.Uri
import androidx.room.TypeConverter

/**
 * Converts [Uri] objects to [String] and vice versa for Room database storage.
 */
class UriTypeConverter {

    @TypeConverter
    fun toString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun fromString(string: String?): Uri? = string?.let(Uri::parse)
}