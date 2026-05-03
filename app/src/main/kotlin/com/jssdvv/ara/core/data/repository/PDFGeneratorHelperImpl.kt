package com.jssdvv.ara.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PDFGeneratorHelper
import com.jssdvv.ara.machines.domain.model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

class PDFGeneratorHelperImpl(
    private val context: Context,
    private val directoriesManager: DirectoriesManager,
    private val filesManager: FilesManager,
) : PDFGeneratorHelper {
    companion object {
        // PDF properties
        // Paper size (8.5 in x 11 in) in points (1 point = 1/72 inch)
        private const val SHEET_WIDTH_PTS = 612
        private const val SHEET_HEIGHT_PTS = 792

        // Margins in points (All margins are equal)
        private const val SHEET_MARGIN_PTS = 36

        // Cm to points ratio for marker size conversion
        private const val CM_TO_PTS_RATIO = 28.35F

        // Distance between rows (0.25 in) in points
        private const val COLUMN_SPACING_PTS = 18
    }

    private val usableWidth by lazy { SHEET_WIDTH_PTS - 2 * SHEET_MARGIN_PTS.toFloat() }
    private val usableHeight by lazy { SHEET_HEIGHT_PTS - 2 * SHEET_MARGIN_PTS.toFloat() }

    override suspend fun renderPDF(uri: Uri?, onRenderer: (PdfRenderer) -> Unit): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            val descriptor = filesManager.getFileDescriptor(uri) ?: return@withContext emptyList()

            descriptor.use {
                PdfRenderer(it).use { renderer ->
                    onRenderer(renderer)
                    (0 until renderer.pageCount).map { index ->
                        renderer.openPage(index).use { page ->
                            createBitmap(page.width, page.height).also { bitmap ->
                                Canvas(bitmap).drawColor(Color.WHITE)
                                page.render(bitmap, null, null, RENDER_MODE_FOR_DISPLAY)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun generateTechnicalSheetPDF() {
        TODO("Generate it from machine details")
    }

    override suspend fun generateMarkersPDF(
        fileName: String,
        machineId: Int,
        markers: List<Marker>,
    ): Uri {
        val pdfUri = withContext(Dispatchers.IO) {

            if (markers.isEmpty()) {
                throw IllegalStateException("No markers found for machine with id $machineId")
            }

            val document = PdfDocument()
            var currentPage: PdfDocument.Page? = null
            var pageInfo: PageInfo?
            var canvas: Canvas? = null
            var currentPageNum = 1

            var currentX = SHEET_MARGIN_PTS.toFloat()
            var currentY = SHEET_MARGIN_PTS.toFloat()
            var rowHeight = 0F

            for (marker in markers) {
                try {
                    val markerInputStream = filesManager.getInputStream(marker.imageUri)
                    val markerOriginalBitmap = filesManager.getBitmap(markerInputStream)
                        ?: throw IllegalStateException("Failed to load bitmap from uri: ${marker.imageUri}")

                    var markerWidthPts =
                        (marker.sizeCentimeters.coerceAtLeast(5F) * CM_TO_PTS_RATIO)

                    val markerHeightPts =
                        markerOriginalBitmap.height * markerWidthPts / markerOriginalBitmap.width

                    // If the marker placed in the current row doesn't fit, creates a new row.
                    if (currentX + markerWidthPts > SHEET_MARGIN_PTS + usableWidth) {

                        // Reset the current X position.
                        currentX = SHEET_MARGIN_PTS.toFloat()

                        // Starts a new row.
                        currentY += rowHeight + COLUMN_SPACING_PTS
                    }

                    // Ensures that the marker width is not bigger than the page width.
                    if (markerWidthPts > usableWidth) {
                        markerWidthPts = usableWidth
                    }

                    // If the marker height is bigger than the page height, we need to create a new
                    // page.
                    if (currentY + markerWidthPts > SHEET_MARGIN_PTS + usableHeight || currentPage == null) {

                        if (currentPage != null) document.finishPage(currentPage)

                        // Create new page
                        pageInfo = PageInfo.Builder(
                            SHEET_WIDTH_PTS,
                            SHEET_HEIGHT_PTS,
                            currentPageNum
                        ).create()

                        currentPage = document.startPage(pageInfo)
                        canvas = currentPage.canvas

                        // Reset the current position.
                        currentY = SHEET_MARGIN_PTS.toFloat()
                        currentX = SHEET_MARGIN_PTS.toFloat()
                        rowHeight = 0F
                        currentPageNum++
                    }

                    canvas?.let {

                        val scaledBitmap = markerOriginalBitmap.scale(
                            markerWidthPts.toInt(),
                            markerHeightPts.toInt()
                        )

                        it.drawBitmap(scaledBitmap, currentX, currentY, null)

                        scaledBitmap.recycle()
                        markerOriginalBitmap.recycle()
                    }

                    currentX += markerWidthPts + COLUMN_SPACING_PTS
                    rowHeight = max(rowHeight, markerHeightPts)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (currentPage != null) document.finishPage(currentPage)

            val pdfDir = directoriesManager.getDocumentsDir(machineId)

            val filePath = File(pdfDir, fileName)
            FileOutputStream(filePath).use { document.writeTo(it) }
            document.close()

            Uri.fromFile(File(directoriesManager.getDocumentsDir(machineId), fileName))
        }

        withContext(Dispatchers.Main) {
            filesManager.shareFile(pdfUri)
        }

        return pdfUri
    }
}