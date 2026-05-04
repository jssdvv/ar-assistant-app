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
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.jssdvv.ara.core.domain.repository.DirectoriesManager
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PDFHelper
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.presentation.destination.documents.DocumentSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

class PDFHelperImpl(
    private val context: Context,
    private val directoriesManager: DirectoriesManager,
    private val filesManager: FilesManager,
) : PDFHelper {
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

    private val rendererMutex = Mutex()

    override suspend fun openRenderer(uri: Uri, onRenderer: (PdfRenderer) -> Unit): Int {
        return withContext(Dispatchers.IO) {
            rendererMutex.withLock {
                val descriptor = filesManager.getFileDescriptor(uri) ?: return@withContext 0
                PdfRenderer(descriptor).also(onRenderer).pageCount
            }
        }
    }

    override suspend fun closeRenderer(renderer: PdfRenderer?) {
        withContext(Dispatchers.IO) {
            rendererMutex.withLock { runCatching { renderer?.close() } }
        }
    }

    override suspend fun renderPage(renderer: PdfRenderer?, index: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            rendererMutex.withLock {
                renderer?.openPage(index)?.use { page ->
                    createBitmap(page.width, page.height).also { bitmap ->
                        Canvas(bitmap).drawColor(Color.WHITE)
                        page.render(bitmap, null, null, RENDER_MODE_FOR_DISPLAY)
                    }
                }
            }
        }
    }

    override suspend fun getPageSizes(renderer: PdfRenderer?): Map<Int, IntSize> {
        if (renderer == null) return emptyMap()
        return withContext(Dispatchers.IO) {
            (0 until renderer.pageCount).associate { index ->
                rendererMutex.withLock {
                    renderer.openPage(index).use { page ->
                        index to IntSize(page.width, page.height)
                    }
                }
            }
        }
    }

    override suspend fun searchInPDF(
        renderer: PdfRenderer?,
        query: String,
        pageCount: Int,
    ): List<DocumentSearchResult> {
        if (query.isBlank() || renderer == null) return emptyList()
        return withContext(Dispatchers.IO) {
            (0 until pageCount).flatMap { index ->
                rendererMutex.withLock {
                    renderer.openPage(index).use { page ->
                        page.searchText(query).map { bounds ->
                            DocumentSearchResult(
                                page = index,
                                bounds = bounds,
                            )
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
                    val markerOriginalBitmap = filesManager.getBitmap(marker.imageUri)
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