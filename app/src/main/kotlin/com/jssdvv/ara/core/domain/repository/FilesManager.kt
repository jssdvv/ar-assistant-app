package com.jssdvv.ara.core.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.jssdvv.ara.core.domain.type.UriType
import java.io.File
import java.io.InputStream

interface FilesManager {
    fun getUriType(uri: Uri): UriType
    fun getFileNameFromUri(uri: Uri?): String?
    fun getInputStreamFromUri(uri: Uri?): InputStream?
    fun getSvgTextPaths(svgUri: Uri = Uri.EMPTY) : Array<String>
    fun getBitmapFromInputStream(inputStream: InputStream?): Bitmap?

    fun copyModelToInternalStorage(
        contentUri: Uri,
        machineId: Int
    ): File?

    fun copyImageToInternalStorage(
        contentUri: Uri,
        machineId: Int
    ): File?

    fun copyPdfToInternalStorage(
        contentUri: Uri,
        machineId: Int
    ): File?

    suspend fun generateLabelBitmap(
        name: String,
        description: String,
        sizeCentimeters: Float,
        contentUriImage: Uri?,
        machineId: Int
    ): Uri?

    fun deleteFile(file: File)
}