package com.jssdvv.ara.core.domain.type

import android.content.Intent
import android.content.res.AssetManager
import android.media.MediaPlayer
import androidx.core.content.FileProvider

enum class UriType(
    val scheme: String,
    val prefix: String? = null,
) {
    /**
     * Used for accessing app resources (drawable, raw, etc).
     * Commonly used for playing sounds via [MediaPlayer].
     *
     * Format: ```android.resource://[package]/[id]```
     */
    RESOURCE(
        scheme = "android.resource"
    ),

    /**
     * Used for accessing files bundled in the assets folder of the APK.
     * Resolved via [AssetManager], not the file system.
     * Typically used for pre-populating internal storage on first launch.
     *
     * Format: ```file:///android_asset/[path]/[file]```
     */
    ASSET(
        scheme = "file",
        prefix = "android_asset"
    ),

    /**
     * Used for accessing data from the local file system.
     * Note: restricted on Android 7+ without [FileProvider].
     *
     * Format: ```file:///[path]/[file]```
     */
    FILE(
        scheme = "file"
    ),

    /**
     * Used for accessing data from a ContentProvider.
     * This is the standard scheme received from [Intent.ACTION_OPEN_DOCUMENT]
     * and [Intent.ACTION_GET_CONTENT] file pickers.
     *
     * Format: ```content://[authority]/[path]/[file]```
     */
    CONTENT(
        scheme = "content"
    )
}