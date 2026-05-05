package com.jssdvv.ara.core.domain.repository

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.ui.unit.IntSize
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.presentation.destination.documents.DocumentSearchResult

interface PDFHelper {
    suspend fun openRenderer(uri: Uri, onRenderer: (PdfRenderer) -> Unit): Int
    suspend fun closeRenderer(renderer: PdfRenderer?)
    suspend fun renderPage(renderer: PdfRenderer?, index: Int): Bitmap?
    suspend fun getPageSizes(renderer: PdfRenderer?): Map<Int, IntSize>
    suspend fun searchInPDF(
        renderer: PdfRenderer?,
        query: String,
        pageCount: Int
    ): List<DocumentSearchResult>

    suspend fun generateDocumentPreview(uri: Uri, machineId: Int): Uri?
    fun generateTechnicalSheetPDF()
    suspend fun generateMarkersPDF(
        fileName: String,
        machineId: Int,
        markers: List<Marker>
    ): Uri
}