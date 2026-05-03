package com.jssdvv.ara.core.domain.repository

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.jssdvv.ara.machines.domain.model.Marker

interface PDFGeneratorHelper {
    suspend fun renderPDF(uri: Uri?, onRenderer: (PdfRenderer) -> Unit): List<Bitmap>
    fun generateTechnicalSheetPDF()
    suspend fun generateMarkersPDF(
        fileName: String,
        machineId: Int,
        markers: List<Marker>
    ): Uri
}