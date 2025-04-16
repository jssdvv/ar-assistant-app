package com.jssdvv.ara.core.data.local.converter

import android.net.Uri
import androidx.room.TypeConverter

/**
 * Converts [Uri] objects to [String] and vice versa for Room database storage.
 */
class UriTypeConverter {

    /**
     * Converts a [Uri] to a [String] for database storage.
     *
     * @param [uri] The [Uri] to convert.
     *
     * @return The [String] representation of the [Uri], or `null` if not provided.
     */
    @TypeConverter
    fun toString(uri: Uri?): String? = uri?.toString()

    /**
     * Converts a [String] back to a [Uri].
     *
     * @param [string] The [String] representation of a [Uri].
     *
     * @return The [Uri] object or `null` if the string is `null`.
     */
    @TypeConverter
    fun fromString(string: String?): Uri? = string?.let(Uri::parse)
}