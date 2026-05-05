package com.jssdvv.ara.core.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.jssdvv.ara.core.domain.type.UriType
import java.io.File
import java.io.InputStream

interface FilesManager {
    fun getUriType(uri: Uri): UriType?
    fun getFileName(uri: Uri?): String?
    fun getInputStream(uri: Uri?): InputStream?
    fun getSvgTextPaths(svgUri: Uri = Uri.EMPTY): Array<String>
    fun getBitmap(uri: Uri?): Bitmap?
    fun copyModelToInternalStorage(uri: Uri, machineId: Int): File?
    fun copyImageToInternalStorage(uri: Uri, machineId: Int): File?
    fun copyDocToInternalStorage(uri: Uri, machineId: Int): File?
    fun getFileDescriptor(uri: Uri?): ParcelFileDescriptor?
    fun saveBitmapToInternalStorage(bitmap: Bitmap, targetDir: File, rename: String? = null): File?

    fun saveMarkerToInternalStorage(bitmap: Bitmap, machineId: Int, rename: String? = null): File?
    fun getShareableUri(uri: Uri?): Uri?
    fun shareFile(uri: Uri?)
    fun deleteFile(file: File)
}