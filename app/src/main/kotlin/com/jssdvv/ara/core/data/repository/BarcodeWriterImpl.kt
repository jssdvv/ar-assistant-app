package com.jssdvv.ara.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.repository.BarcodeWriter

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
    val darkCellColor: Int = Color.BLACK,
    val lightCellColor: Int = Color.WHITE,
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.L,
) : BarcodeWriter {

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
        return Bitmap.createBitmap(
            sideLength,
            sideLength,
            bitmapConfig
        ).apply {

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
}