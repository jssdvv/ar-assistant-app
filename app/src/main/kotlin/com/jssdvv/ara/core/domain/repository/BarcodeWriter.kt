package com.jssdvv.ara.core.domain.repository

import android.graphics.Bitmap

interface BarcodeWriter {
    fun getQRCodeBitmap(text: String, sideLength: Int): Bitmap
    fun generateMarkerBitmap(displayName: String, bitmap: Bitmap): Bitmap
    fun deleteBitmapFromInternalStorage(machineId: Int, displayName: String)
}