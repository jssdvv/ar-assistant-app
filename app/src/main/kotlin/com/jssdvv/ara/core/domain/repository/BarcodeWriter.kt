package com.jssdvv.ara.core.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface BarcodeWriter {
    fun getQRCodeBitmap(
        text: String,
        sideLength: Int,
    ): Bitmap

    fun saveBitmapToInternalStorage(
        machineId: Int,
        displayName: String,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat?,
        quality: Int?
    ): Uri

    fun deleteBitmapFromInternalStorage(
        machineId: Int,
        displayName: String
    )
}