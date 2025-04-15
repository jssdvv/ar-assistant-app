package com.jssdvv.ara.core.domain.repository

import android.graphics.Bitmap

interface BarcodeWriter {
    fun getQRCodeBitmap(
        text: String,
        sideLength: Int,
    ): Bitmap
}