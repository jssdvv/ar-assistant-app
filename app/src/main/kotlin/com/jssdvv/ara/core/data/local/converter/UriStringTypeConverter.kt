package com.jssdvv.ara.core.data.local.converter

import android.net.Uri
import androidx.room.TypeConverter

/**
 * `UriStringTypeConverter` provides methods to convert `Uri` objects to `String`
 * representations and vice versa. This is essential for storing `Uri` data in a
 * Room database, as Room only supports a limited range of data types directly. By
 * converting `Uri` to `String`, this class enables `Uri` data to be stored as text
 * in the database, and later re-converted for use in the application.
 *
 * Methods:
 * - `uriToString(uri: Uri): String` - Converts a `Uri` object to its `String` representation.
 * - `stringToUri(string: String): Uri` - Converts a `String` representation back to a `Uri` object.
 */
class UriStringTypeConverter {

    /**
     * Converts a `Uri` object to a `String`.
     *
     * @param uri The `Uri` to be converted.
     * @return A `String` representation of the `Uri`, which can be stored in the database.
     */
    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }

    /**
     * Converts a `String` to a `Uri` object.
     *
     * @param string The `String` representation of a `Uri` to be converted.
     * @return A `Uri` object constructed from the `String`.
     */
    @TypeConverter
    fun stringToUri(string: String?): Uri? {
        return string?.let { Uri.parse(it) }
    }
}
