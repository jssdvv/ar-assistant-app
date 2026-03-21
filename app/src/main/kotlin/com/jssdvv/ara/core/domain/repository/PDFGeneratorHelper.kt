package com.jssdvv.ara.core.domain.repository

import android.net.Uri
import com.jssdvv.ara.machines.domain.model.Marker

interface PDFGeneratorHelper {
    fun generateTechnicalSheetPDF()
    suspend fun generateMarkersPDF(
        fileName: String,
        machineId: Int,
        markers: List<Marker>
    ): Uri
}