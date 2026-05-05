package com.jssdvv.ara.core.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.type.FileType
import com.jssdvv.ara.core.domain.type.UriType
import com.jssdvv.ara.core.domain.utility.fileExtension
import com.jssdvv.ara.core.domain.utility.randomFileName
import com.jssdvv.ara.core.domain.utility.requireExtension
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class FilesManagerImpl(
    private val context: Context,
    private val directoriesManager: DirectoriesManager,
) : FilesManager {
    companion object {
        private const val PDF_PREVIEW_DPI = 300
        private const val PDF_PREVIEW_MAX_WIDTH = 2000
    }

    override fun getUriType(uri: Uri): UriType? {
        if (
            uri.scheme == UriType.ASSET.scheme &&
            uri.pathSegments.firstOrNull() == UriType.ASSET.prefix
        ) return UriType.ASSET

        return UriType.entries
            .filter { it.prefix == null }
            .firstOrNull { it.scheme == uri.scheme }
    }

    override fun getInputStream(uri: Uri?): InputStream? {
        if (uri == null) return null
        val uriType = getUriType(uri) ?: return null
        return try {
            when (uriType) {
                UriType.RESOURCE -> context.resources.openRawResource(
                    uri.pathSegments.lastOrNull()?.toIntOrNull() ?: return null
                )

                UriType.ASSET -> context.assets.open(uri.pathSegments.drop(1).joinToString("/"))
                UriType.FILE -> uri.path?.let { FileInputStream(it) }
                UriType.CONTENT -> context.contentResolver.openInputStream(uri)
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to get input stream from Uri: $uri", e)
        }
    }

    override fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        val uriType = getUriType(uri) ?: return null
        return try {
            when (uriType) {
                UriType.RESOURCE,
                UriType.ASSET -> uri.pathSegments.lastOrNull()

                UriType.FILE -> File(uri.path ?: return null).name
                UriType.CONTENT -> {
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIndex != -1) {
                            cursor.getString(nameIndex)
                        } else null
                    }
                }
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to get file name from Uri: $uri", e)
        }
    }

    override fun getBitmap(uri: Uri?): Bitmap? {
        return getInputStream(uri).use { BitmapFactory.decodeStream(it) }
    }

    override fun getSvgTextPaths(
        svgUri: Uri
    ): Array<String> {
        val rawText = getInputStream(svgUri)
            ?.bufferedReader()
            ?.use { it.readText() }

        val regex = Regex("""<path[^>]*\bd="([^"]+)""")

        return rawText?.let { svgText ->
            regex.findAll(svgText)
                .map { it.groupValues[1] }
                .toList()
                .toTypedArray()
        } ?: emptyArray()
    }

    private fun copyFileToInternalStorage(
        uri: Uri,
        defaultName: String,
        fileType: FileType,
        targetDir: File,
    ): File? {
        val inputStream = getInputStream(uri) ?: return null
        val fileName = getFileName(uri) ?: defaultName
        val extension = fileName.fileExtension().also { it.requireExtension(fileType) }
        val storageFileName = extension.randomFileName()

        return File(targetDir, storageFileName).also { outputFile ->
            inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override fun copyImageToInternalStorage(uri: Uri, machineId: Int): File? {
        return copyFileToInternalStorage(
            uri = uri,
            defaultName = "image",
            fileType = FileType.IMAGE,
            targetDir = directoriesManager.getMediaFilesDir(machineId),
        )
    }

    override fun copyModelToInternalStorage(uri: Uri, machineId: Int): File? {
        return copyFileToInternalStorage(
            uri = uri,
            defaultName = "model",
            fileType = FileType.MODEL,
            targetDir = directoriesManager.getModelsDir(machineId),
        )
    }

    override fun copyDocToInternalStorage(uri: Uri, machineId: Int): File? {
        return copyFileToInternalStorage(
            uri = uri,
            defaultName = "document",
            fileType = FileType.PDF,
            targetDir = directoriesManager.getDocumentsDir(machineId),
        )
    }

    private fun Bitmap.toRoundedBitmap(cornerRadius: Float): Bitmap {
        return createBitmap(width, height).also { output ->
            Canvas(output).drawRoundRect(
                RectF(0f, 0f, width.toFloat(), height.toFloat()),
                cornerRadius,
                cornerRadius,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = BitmapShader(
                        this@toRoundedBitmap,
                        Shader.TileMode.CLAMP,
                        Shader.TileMode.CLAMP
                    )
                }
            )
        }
    }

    override fun saveBitmapToInternalStorage(
        bitmap: Bitmap,
        targetDir: File,
        rename: String?
    ): File? {
        return runCatching {
            val fileName = rename?.let { "$it.png" } ?: "png".randomFileName()
            File(targetDir, fileName).also { file ->
                file.outputStream().use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        }.getOrNull()
    }

    override fun saveMarkerToInternalStorage(
        bitmap: Bitmap,
        machineId: Int,
        rename: String?,
    ): File? {
        return saveBitmapToInternalStorage(
            bitmap = bitmap,
            targetDir = directoriesManager.getMarkersDir(machineId),
            rename = rename,
        )
    }

    override fun getFileDescriptor(uri: Uri?): ParcelFileDescriptor? {
        if (uri == null) return null
        val uriType = getUriType(uri) ?: return null
        return when (uriType) {
            UriType.CONTENT -> context.contentResolver.openFileDescriptor(uri, "r")
            UriType.FILE -> uri.path?.let {
                ParcelFileDescriptor.open(
                    File(it),
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            }

            UriType.ASSET,
            UriType.RESOURCE -> {
                File(context.cacheDir, "tmp_${uri.lastPathSegment}").let { tempFile ->
                    getInputStream(uri)?.use { input ->
                        tempFile.outputStream().use { input.copyTo(it) }
                    }
                    ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).also {
                        tempFile.delete()
                    }
                }
            }
        }
    }

    override fun getShareableUri(uri: Uri?): Uri? {
        if (uri == null) return null
        val uriType = getUriType(uri) ?: return null
        return when (uriType) {
            UriType.CONTENT -> uri
            UriType.FILE -> runCatching {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    File(uri.path ?: return null)
                )
            }.getOrNull()

            UriType.ASSET,
            UriType.RESOURCE -> runCatching {
                val tempFile = File(context.cacheDir, "tmp_${uri.lastPathSegment}")
                getInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { input.copyTo(it) }
                }
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    tempFile
                ).also { tempFile.delete() }
            }.getOrNull()
        }
    }

    override fun shareFile(uri: Uri?) {
        if (uri == null) return
        val extension = getFileName(uri)?.fileExtension() ?: return
        val fileType = FileType.fromExtension(extension) ?: return
        val shareableUri = getShareableUri(uri) ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = fileType.mimeType
            putExtra(Intent.EXTRA_STREAM, shareableUri)
//            putExtra(Intent.EXTRA_SUBJECT, context.getString(fileType.shareSubject))
//            putExtra(Intent.EXTRA_TEXT, context.getString(fileType.shareText))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, null).apply {
            if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    override fun deleteFile(file: File) {
        if (file.exists()) file.delete()
    }
}