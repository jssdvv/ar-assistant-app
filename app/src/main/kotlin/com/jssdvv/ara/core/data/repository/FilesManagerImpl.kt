package com.jssdvv.ara.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.withTranslation
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.type.FileType
import com.jssdvv.ara.core.domain.type.UriType
import com.jssdvv.ara.core.domain.utility.fileExtension
import com.jssdvv.ara.core.domain.utility.randomFileName
import com.jssdvv.ara.core.domain.utility.requireExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override fun getBitmap(inputStream: InputStream?): Bitmap? {
        return inputStream?.use { BitmapFactory.decodeStream(it) }
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

    override suspend fun generateLabelBitmap(
        name: String,
        description: String,
        sizeCentimeters: Float,
        contentUriImage: Uri?,
        machineId: Int,
    ): Uri? = withContext(Dispatchers.IO) {

        val density = context.resources.displayMetrics.density
        val padding = 32F * density
        val realWidthPx = 300
        val widthRatio = realWidthPx / maxOf(sizeCentimeters, 15F)

        val imageBitmap = try {
            getBitmap(getInputStream(contentUriImage))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        val imageHeightPx = imageBitmap
            ?.let { realWidthPx * it.height / it.width.toFloat() }
            ?: 0F

        val textWidth = realWidthPx - 2 * padding
        val textSizeTitlePx = (if (name.isBlank()) 0F else 2F) * widthRatio
        val textSizeDescriptionPx = (if (description.isBlank()) 0F else 1.5F) * widthRatio

        val paintTitle = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = textSizeTitlePx
            typeface = Typeface.DEFAULT_BOLD
        }

        val paintDescription = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = textSizeDescriptionPx
            typeface = Typeface.DEFAULT
        }

        val titleLayout = StaticLayout.Builder
            .obtain(name, 0, name.length, paintTitle, textWidth.toInt())
            .setAlignment((Layout.Alignment.ALIGN_CENTER))
            .build()

        val descriptionLayout = StaticLayout.Builder
            .obtain(description, 0, description.length, paintDescription, textWidth.toInt())
            .setAlignment((Layout.Alignment.ALIGN_NORMAL))
            .build()

        val totalHeightPx = imageHeightPx +
                titleLayout.height +
                descriptionLayout.height +
                3 * padding

        val bitmap = createBitmap(realWidthPx, totalHeightPx.toInt())
        val canvas = Canvas(bitmap)

        canvas.drawRoundRect(
            RectF(0f, 0f, realWidthPx.toFloat(), totalHeightPx),
            24f * density,
            24f * density,
            Paint().apply { color = Color.WHITE; isAntiAlias = true }
        )

        imageBitmap?.let {
            canvas.drawBitmap(it.scale(realWidthPx, imageHeightPx.toInt()), 0F, 0F, null)
        }

        canvas.withTranslation(0F, imageHeightPx) { titleLayout.draw(this) }
        canvas.withTranslation(
            0F,
            imageHeightPx + titleLayout.height + padding
        ) { descriptionLayout.draw(this) }

        val outputFile =
            File(directoriesManager.getMediaFilesDir(machineId), "webp".randomFileName())

        outputFile.outputStream().use { outputStream ->
            bitmap
                .toRoundedBitmap(15F * density)
                .compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
        }

        Uri.fromFile(outputFile)
    }

    override fun deleteFile(file: File) {
        if (file.exists()) file.delete()
    }
}