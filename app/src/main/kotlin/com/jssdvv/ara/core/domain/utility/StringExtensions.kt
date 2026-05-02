package com.jssdvv.ara.core.domain.utility

import com.jssdvv.ara.core.domain.type.FileType
import java.util.UUID

/**
 * Validates that this file extension is allowed for the given [FileType].
 *
 * @receiver The file extension to validate (e.g. `"jpg"`, `"pdf"`).
 * @throws IllegalArgumentException if the extension is not in [FileType.extensions].
 */
fun String.requireExtension(fileType: FileType) {
    require(fileType.extensions.contains(this)) {
        "File extension '$this' must be one of: ${fileType.extensions}"
    }
}

/**
 * Extracts the file extension from this file name.
 *
 * @receiver The file name to extract the extension from (e.g. `"image.jpg"`).
 * @return The file extension without the dot (e.g. `"jpg"`), or an empty string if none found.
 */
fun String.fileExtension(): String = substringAfterLast('.', "")

/**
 * Generates a random file name using [UUID] with this string as the extension.
 *
 * @receiver The file extension to use (e.g. `"jpg"`).
 * @return A random file name (e.g. `"a1b2c3d4-...-e5f6.jpg"`).
 */
fun String.randomFileName(): String = "${UUID.randomUUID()}.$this"