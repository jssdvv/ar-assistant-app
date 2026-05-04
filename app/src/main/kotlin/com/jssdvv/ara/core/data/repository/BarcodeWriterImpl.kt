package com.jssdvv.ara.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.repository.BarcodeWriter
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import java.io.File
import kotlin.random.Random

/**
 * Helper class for generating QR codes as bitmaps.
 *
 * @property [darkCellColor] The color used for the dark cells of the QR code.
 * @property [lightCellColor] The color used for the light cells of the QR code.
 * @property [errorCorrectionLevel] The error correction level used for the QR code generation.
 */
class BarcodeWriterImpl(
    private val context: Context,
    private val bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888,
    private val directoriesManager: DirectoriesManager,
    var darkCellColor: Int = Color.BLACK,
    val lightCellColor: Int = Color.WHITE,
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.L,
) : BarcodeWriter {

    companion object {
        private val DEFAULT_FORMAT = Bitmap.CompressFormat.PNG
        private const val DEFAULT_QUALITY = 100
    }

    private val qrCodeWriter by lazy { QRCodeWriter() }

    /**
     * Generate a QR code from the given text.
     *
     * @param [text] the text to be encoded into the QR code
     * @param [sideLength] the length of each side of the generated QR code
     *
     * @return The generated QR code as a [Bitmap]
     *
     * @throws [IllegalArgumentException] if the text is empty or null
     * @throws [IllegalArgumentException] if the side length is less than or equal to zero
     */
    private fun getQRCodeIntArray(
        text: String,
        sideLength: Int,
        randomQRColor: Boolean = false
    ): IntArray {

        require(text.isNotBlank()) { context.getString(R.string.barcode_writer_required_content_error) }
        require(sideLength > 0) { context.getString(R.string.barcode_writer_required_length_error) }

        val hints = mapOf(EncodeHintType.ERROR_CORRECTION to errorCorrectionLevel)
        val bitMatrix = qrCodeWriter.encode(
            text,
            BarcodeFormat.QR_CODE,
            sideLength,
            sideLength,
            hints
        )

        if(randomQRColor) {
            val hsv = FloatArray(3)
            hsv[0] = (Random.nextFloat() * 360).coerceIn(0F, 360F)
            hsv[1] = 1F
            hsv[2] = 0.5F
            darkCellColor = Color.HSVToColor(hsv)
        }

        // Creates a 2D array of ints representing the QR code,
        // with true for dark cells and false for light cells.
        return IntArray(sideLength * sideLength).also { pixels ->
            var pixel = 0
            for (y in 0 until sideLength) {
                for (x in 0 until sideLength) {
                    pixels[pixel++] = if (bitMatrix[x, y]) darkCellColor else lightCellColor
                }
            }
        }
    }

    /**
     * Encodes a given text into a QR code and returns it as a bitmap.
     *
     * @param [text] The text to encode.
     * @param [sideLength] The desired side length of the QR code in pixels.
     *
     * @return A [Bitmap] containing the QR code.
     *
     * @throws [IllegalArgumentException] if [sideLength] is less than or equal to zero.
     */
    override fun getQRCodeBitmap(
        text: String,
        sideLength: Int,
    ): Bitmap {
        val pixels = getQRCodeIntArray(text, sideLength)
        return createBitmap(sideLength, sideLength, bitmapConfig).apply {

            // Set the pixels of the bitmap with the int array of pixels.
            // The parameters to this function are:
            //      pixels: The array of pixels to set.
            //      offset: The number of pixels to skip before starting to fill the bitmap.
            //      stride: The number of pixels between the start of two successive rows of pixels.
            //      x: The leftmost column of the pixels to be filled.
            //      y: The topmost row of the pixels to be filled.
            //      width: The number of pixels in the x direction to be filled.
            //      height: The number of pixels in the y direction to be filled.
            setPixels(pixels, 0, sideLength, 0, 0, sideLength, sideLength)
        }
    }

    override fun saveBitmapToInternalStorage(
        machineId: Int,
        displayName: String,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat?,
        quality: Int?,
    ): Uri {
        val dir = directoriesManager.getMarkersDir(machineId)
        val actualFormat = format ?: DEFAULT_FORMAT
        val actualQuality = quality ?: DEFAULT_QUALITY
        val density = context.resources.displayMetrics.density

        val padding = 5F * density
        val cornerRadius = 8F * density
        val textSize = 40F * density
        val spacingBetweenQrAndText = 10F * density
        val strokeWidth = 2F * density

        val textPaint = Paint().apply {
            this.textSize = textSize
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
            isDither = true
        }

        val textBounds = Rect().apply {
            textPaint.getTextBounds(displayName, 0, displayName.length, this)
        }
        val textHeight = textBounds.height().toFloat()
        val textWidth = textPaint.measureText(displayName)

        val finalWidth = maxOf(bitmap.width + padding * 2, textWidth + padding * 2)
        val finalHeight = bitmap.height + padding * 2 + spacingBetweenQrAndText + textSize

        val newBitmap = createBitmap(finalWidth.toInt(), finalHeight.toInt())

        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            isAntiAlias = true
            isDither = true
        }

        val halfStrokeWidth = strokeWidth / 2
        val roundedRect = RectF(
            halfStrokeWidth,
            halfStrokeWidth,
            finalWidth - halfStrokeWidth,
            finalHeight - halfStrokeWidth
        )

        val qrLeft = (finalWidth - bitmap.width) / 2

        Canvas(newBitmap).apply {
            // Background color
            drawColor(Color.WHITE)

            // QR code
            drawBitmap(
                bitmap,
                qrLeft,
                padding,
                null
            )

            // Border Rect
            drawRoundRect(
                roundedRect,
                cornerRadius,
                cornerRadius,
                borderPaint
            )

            // Display name below the QR
            if (displayName.isNotBlank()) {
                drawText(
                    displayName,
                    finalWidth / 2,
                    padding + bitmap.height + spacingBetweenQrAndText + textHeight / 2,
                    textPaint
                )
            }
        }

        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$displayName.${actualFormat.name.lowercase()}")

        return try {
            file.outputStream().use { newBitmap.compress(actualFormat, actualQuality, it) }
            Uri.fromFile(file)
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }

    override fun deleteBitmapFromInternalStorage(
        machineId: Int,
        displayName: String
    ) {
        try {
            val dir = directoriesManager.getMarkersDir(machineId)
            val file = File(dir, displayName)
            file.delete()
        } catch (e: Exception) {
            throw e
        }
    }
}