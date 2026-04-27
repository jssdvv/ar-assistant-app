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
import android.util.Base64
import android.widget.Toast
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.withTranslation
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.type.UriType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class FilesManagerImpl(
    private val context: Context,
    private val directoriesManager: DirectoriesManager,
) : FilesManager {
    companion object {

        private const val MODEL_EXTENSION = "glb"
        private const val SVG_EXTENSION = "svg"
        private const val ASSETS_PREFIX = "/android_asset/"

        val validImageExtensions = setOf(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "bmp",
            "webp",
            "heic",
            "heif",
            "tiff"
        )

        private const val PDF_EXTENSION = "pdf"
        private const val PDF_PREVIEW_DPI = 300
        private const val PDF_PREVIEW_MAX_WIDTH = 2000
    }

    override fun getUriType(uri: Uri): UriType =
        UriType.entries.firstOrNull { it.schemes.contains(uri.scheme) } ?: UriType.CONTENT

    override fun getFileNameFromUri(uri: Uri?): String? {
        if (uri == null) return null
        val uriType = getUriType(uri)
        return try {
            when (uriType) {
                UriType.RESOURCE -> uri.path?.split('/')?.lastOrNull()
                UriType.FILE -> File(uri.path ?: return null).name
                UriType.DATA -> uri.schemeSpecificPart?.split('/')?.lastOrNull()

                UriType.CONTENT,
                UriType.NETWORK,
                UriType.MARKET,
                UriType.PACKAGE,
                    -> {
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIndex != -1) {
                            cursor.getString(nameIndex)
                        } else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("Failed to load scheme ${uri.scheme} from Uri: $uri")
        }
    }

    override fun getBitmapFromInputStream(inputStream: InputStream?): Bitmap? =
        inputStream?.use { BitmapFactory.decodeStream(it) }

    override fun getInputStreamFromUri(uri: Uri?): InputStream? {
        if (uri == null) return null
        val uriType = getUriType(uri)
        return try {
            when (uriType) {
                UriType.RESOURCE -> {
                    val resourceId = uri.toString().split('/').last().toIntOrNull()
                        ?: return null
                    context.resources.openRawResource(resourceId)
                }

                UriType.FILE -> {
                    uri.path?.let { path ->
                        if (path.startsWith(ASSETS_PREFIX)) {
                            context.assets.open(path.removePrefix(ASSETS_PREFIX))
                        } else {
                            FileInputStream(path)
                        }
                    }
                }

                UriType.DATA -> {
                    val data = uri.schemeSpecificPart ?: return null

                    val commaIndex = data.indexOf(',')
                    if (commaIndex == -1) return null

                    val mimePart = data.substring(0, commaIndex)
                    val mediaType = mimePart.substringBefore(';')
                    val isSupported =
                        validImageExtensions.any { it.equals(mediaType, ignoreCase = true) }
                    if (!isSupported) return null

                    val isBase64 = mimePart.contains("base64", ignoreCase = true)
                    if (!isBase64) return null

                    val base64 = data.substringAfter(',')
                    val byteArray = Base64.decode(base64, Base64.DEFAULT)
                    ByteArrayInputStream(byteArray)
                }

                UriType.CONTENT,
                UriType.NETWORK,
                UriType.MARKET,
                UriType.PACKAGE,
                    -> context.contentResolver.openInputStream(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("Failed to load scheme ${uri.scheme} from Uri: $uri")
        }
    }

    override fun getSvgTextPaths(
        svgUri: Uri
    ) : Array<String> {
        val rawText = getInputStreamFromUri(svgUri)
            ?.bufferedReader()
            ?.use { it.readText() }

        val regex = Regex("""<path[^>]*\bd="([^"]+)""")

        return rawText?.let{ svgText ->
            regex.findAll(svgText)
                .map { it.groupValues[1] }
                .toList()
                .toTypedArray()
        } ?: emptyArray()
    }

    override fun copyModelToInternalStorage(
        contentUri: Uri,
        machineId: Int,
    ): File? {
        try {
            val inputStream = getInputStreamFromUri(contentUri) ?: return null
            val fileName = getFileNameFromUri(contentUri) ?: "model"
            val fileExtension = fileName.substringAfterLast('.', "")

            if (fileExtension != MODEL_EXTENSION)
                throw IllegalArgumentException("File extension must be $MODEL_EXTENSION")

            val storageFileName = "${UUID.randomUUID()}.$MODEL_EXTENSION"

            val modelsDir = directoriesManager.getModelsDir(machineId)
            val outputFile = File(modelsDir, storageFileName)

            inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            return outputFile
        } catch (e: IllegalArgumentException) {
            val toast = Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
            toast.show()
            return null
        }
    }

    override fun copyImageToInternalStorage(
        contentUri: Uri,
        machineId: Int,
    ): File? {
        try {
            val inputStream = getInputStreamFromUri(contentUri) ?: return null
            val fileName = getFileNameFromUri(contentUri) ?: "image"
            val fileExtension = fileName.substringAfterLast('.', "")

            if (!validImageExtensions.any { it == fileExtension })
                throw IllegalArgumentException("File extension must be one of: $validImageExtensions")

            val storageFileName = "${UUID.randomUUID()}.$fileExtension"

            val imagesDir = directoriesManager.getMediaFilesDir(machineId)
            val outputFile = File(imagesDir, storageFileName)

            inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            return outputFile
        } catch (e: IllegalArgumentException) {
            val toast = Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
            toast.show()
            return null
        }
    }

    override fun copyPdfToInternalStorage(
        contentUri: Uri,
        machineId: Int,
    ): File? {
        try {
            val inputStream = getInputStreamFromUri(contentUri) ?: return null
            val fileName = getFileNameFromUri(contentUri) ?: "document"
            val fileExtension = fileName.substringAfterLast('.', "")

            if (fileExtension != PDF_EXTENSION)
                throw IllegalArgumentException("File extension must be $PDF_EXTENSION")

            val storageFileName = "${UUID.randomUUID()}.$PDF_EXTENSION"

            val documentsDir = directoriesManager.getDocumentsDir(machineId)
            val outputFile = File(documentsDir, storageFileName)

            inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            return outputFile
        } catch (e: IllegalArgumentException) {
            val toast = Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
            toast.show()
            return null
        }
    }

    override suspend fun generateLabelBitmap(
        name: String,
        description: String,
        sizeCentimeters: Float,
        contentUriImage: Uri?,
        machineId: Int,
    ): Uri? {
        val uri = withContext(Dispatchers.IO) {

            val density = context.resources.displayMetrics.density

            var canvas: Canvas?
            var bitmap: Bitmap?
            var widthRatio: Float
            var textHeightTitleCm: Float
            var textHeightDescriptionCm: Float
            var imageHeightPx: Float

            try {
                val imageInputStream = getInputStreamFromUri(contentUriImage)
                val imageBitmap = getBitmapFromInputStream(imageInputStream)

                val padding = 32F * density

                val realWidthPx = 300
                if(sizeCentimeters < 15F) {
                    widthRatio = realWidthPx / 15F
                } else {
                    widthRatio = realWidthPx / sizeCentimeters
                }


                // Image Configuration
                val imageWidthPx = realWidthPx
                if(imageBitmap == null) {
                    imageHeightPx = 0F
                } else {
                    imageHeightPx = (imageWidthPx * imageBitmap.height / imageBitmap.width.toFloat())
                }


                // Text Configuration
                val textWidth = realWidthPx - 2 * padding

                textHeightTitleCm = 2f
                textHeightDescriptionCm = 1.5f

                if(name.isBlank()) textHeightTitleCm = 0F
                if(description.isBlank()) textHeightDescriptionCm = 0F

                val textSizeTitlePx = textHeightTitleCm * widthRatio
                val textSizeDescriptionPx = textHeightDescriptionCm * widthRatio

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

                val titleLayout = StaticLayout.Builder.obtain(
                    name,
                    0,
                    name.length,
                    paintTitle,
                    textWidth.toInt()
                )
                    .setAlignment((Layout.Alignment.ALIGN_CENTER))
                    .build()

                val descriptionLayout = StaticLayout.Builder.obtain(
                    description,
                    0,
                    description.length,
                    paintDescription,
                    textWidth.toInt()
                )
                    .setAlignment((Layout.Alignment.ALIGN_NORMAL))
                    .build()

                val titleHeight = titleLayout.height
                val descriptionHeight = descriptionLayout.height

                val contentHeight = titleHeight + descriptionHeight + 3 * padding
                val totalHeightPx = imageHeightPx + contentHeight

                bitmap = createBitmap(imageWidthPx, totalHeightPx.toInt())

                canvas = Canvas(bitmap)

                val backgroundPaint = Paint().apply {
                    color = Color.WHITE
                    isAntiAlias = true
                }

                val backgroundRect = RectF(
                    0f,
                    0f,
                    imageWidthPx.toFloat(),
                    totalHeightPx
                )
                canvas.drawRoundRect(
                    backgroundRect,
                    24f * density,
                    24f * density,
                    backgroundPaint
                )

                imageBitmap?.let{
                    val scaledImage = it.scale(imageWidthPx, imageHeightPx.toInt())

                    canvas.drawBitmap(
                        scaledImage,
                        0F,
                        0F,
                        null
                    )
                }

                canvas.withTranslation(0F, imageHeightPx) {
                    titleLayout.draw(this)
                }

                canvas.withTranslation(0F, imageHeightPx + titleHeight + padding) {
                    descriptionLayout.draw(this)
                }


            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }

            val storageFileName = "${UUID.randomUUID()}.png"

            val imagesDir = directoriesManager.getMediaFilesDir(machineId)
            val outputFile = File(imagesDir, storageFileName)

            outputFile.outputStream().use { outputStream ->
            val roundedBitmap = bitmap.toRoundedBitmap(15F * density)
                roundedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            Uri.fromFile(outputFile)
        }

        return uri
    }

    override fun deleteFile(file: File) {
        if (file.exists()) file.delete()
    }
}

fun Bitmap.toRoundedBitmap(cornerRadius: Float): Bitmap {
    val output = createBitmap(width, height)
    val canvas = Canvas(output)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        shader = BitmapShader(this@toRoundedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

    return output
}