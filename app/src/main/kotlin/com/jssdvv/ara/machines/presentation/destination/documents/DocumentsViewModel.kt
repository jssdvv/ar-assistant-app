package com.jssdvv.ara.machines.presentation.destination.documents

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.models.PageMatchBounds
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PDFHelper
import com.jssdvv.ara.core.domain.type.OrderKey
import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Document
import com.jssdvv.ara.machines.domain.model.DocumentCategory
import com.jssdvv.ara.machines.domain.usecase.DocumentsDataManager
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val documentsDataManager: DocumentsDataManager,
    private val filesManager: FilesManager,
    private val pdfHelper: PDFHelper
) : ViewModel() {

    companion object {
        const val PRELOAD_NEXT_ROWS = 4
        const val RETAIN_PREVIOUS_ROWS = 4
    }

    val machineId: Int = savedStateHandle.toRoute<MachinesGraph.DocumentsRoute>().machineId

    private val orderState = MutableStateFlow(OrderState())
    private val searchDocQuery = MutableStateFlow("")

    private val documents: StateFlow<List<Document>> = documentsDataManager
        .select(machineId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val data: StateFlow<DocumentsData> = combine(
        documents,
        orderState,
        searchDocQuery.debounce(500L)
    ) { documents, orderState, query ->
        val filteredDocuments = documents
            .filter { it.name.contains(query, ignoreCase = true) }
            .let { filtered ->
                when (orderState.type) {
                    OrderType.ASCENDING -> when (orderState.key) {
                        OrderKey.NAME -> filtered.sortedBy { it.name.lowercase() }
                        OrderKey.TYPE -> filtered.sortedBy { it.category }
                        else -> filtered.sortedBy { it.createdAt }
                    }

                    OrderType.DESCENDING -> when (orderState.key) {
                        OrderKey.NAME -> filtered.sortedByDescending { it.name.lowercase() }
                        OrderKey.TYPE -> filtered.sortedByDescending { it.category }
                        else -> filtered.sortedByDescending { it.createdAt }
                    }
                }
            }
        DocumentsData(
            orderState = orderState,
            searchQuery = query,
            filteredDocuments = filteredDocuments
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DocumentsData()
    )

    private var renderer: PdfRenderer? = null
    private var windowSize: Int = 11
    private val openedDocument = MutableStateFlow<Document?>(null)
    private val pageCache = MutableStateFlow<Map<Int, Bitmap>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val renderedDocument: StateFlow<RenderedDocument?> = openedDocument
        .mapLatest { document ->
            pdfHelper.closeRenderer(renderer)
            renderer = null
            pageCache.value = emptyMap()
            searchInDocQuery.value = ""
            document?.fileUri?.let { uri ->
                val pageCount = pdfHelper.openRenderer(uri) { renderer = it }
                val pageSizes = pdfHelper.getPageSizes(renderer)
                RenderedDocument(
                    document = document,
                    pageCount = pageCount,
                    pageSizes = pageSizes,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )


    private val searchInDocQuery = MutableStateFlow("")

    private val renderedDocumentWithSearch: StateFlow<RenderedDocument?> = combine(
        renderedDocument,
        pageCache,
        searchInDocQuery,
        searchInDocQuery.debounce(500L),
    ) { renderedDocument, pageCacheBitmaps, query, debouncedQuery ->
        val searchResults = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            pdfHelper.searchInPDF(renderer, debouncedQuery, renderedDocument?.pageCount ?: 0)
        } else emptyList()

        renderedDocument?.copy(
            pageBitmaps = pageCacheBitmaps,
            search = InDocumentSearchState(query, searchResults)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    val uiState: StateFlow<DocumentsUiState> = combine(
        data,
        renderedDocumentWithSearch,
        DocumentsUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DocumentsUiState.Loading
    )

    fun onEvent(event: DocumentsEvent) {
        when (event) {
            is DocumentsEvent.OnShowDocument -> showDocument(event.document)
            is DocumentsEvent.OnSearchTextInDocument -> searchInDocQuery.value = event.query
            is DocumentsEvent.OnLoadPage -> loadPage(event.index)

            is DocumentsEvent.OnSearchDocuments -> searchDocQuery.value = event.query
            is DocumentsEvent.OnSortDocuments -> sortDocuments(event.orderState)
            is DocumentsEvent.OnUploadDocument -> uploadDocument(
                event.uri,
                event.name,
                event.category
            )

            is DocumentsEvent.OnShareDocument -> shareDocument(event.document)
            is DocumentsEvent.OnDeleteDocument -> deleteDocument(event.document)
            is DocumentsEvent.OnClearShownDocument -> openedDocument.value = null
        }
    }

    private fun loadPage(index: Int) {
        val current = renderedDocument.value ?: return
        val window = getPreloadWindow(index, current.pageCount)

        val evicted = pageCache.value.keys
            .filter { it !in window }
            .fold(pageCache.value) { map, key -> map - key }

        pageCache.value = evicted

        window.forEach { pageIndex ->
            if (pageCache.value.containsKey(pageIndex)) return@forEach
            viewModelScope.launch {
                val bitmap = pdfHelper.renderPage(renderer, pageIndex) ?: return@launch
                pageCache.update { it + (pageIndex to bitmap) }
            }
        }
    }

    private fun getPreloadWindow(currentIndex: Int, pageCount: Int): IntRange {
        val half = windowSize / 2
        val start = (currentIndex - half).coerceAtLeast(0)
        val end = (currentIndex + half).coerceAtMost(pageCount - 1)
        return start..end
    }

    private fun sortDocuments(orderState: OrderState) {
        this.orderState.value = orderState
    }

    private fun showDocument(document: Document) {
        searchInDocQuery.value = ""
        openedDocument.value = document
    }

    private fun uploadDocument(uri: Uri, name: String, category: DocumentCategory) {
        viewModelScope.launch {
            val pdfFile = filesManager.copyDocToInternalStorage(uri, machineId) ?: return@launch
            val pdfUri = Uri.fromFile(pdfFile)
            val previewUri = pdfHelper.generateDocumentPreview(pdfUri, machineId) ?: Uri.EMPTY

            documentsDataManager.upsert(
                Document(
                    machineId = machineId,
                    category = category,
                    name = name,
                    fileUri = pdfUri,
                    previewUri = previewUri,
                )
            )
        }
    }

    private fun shareDocument(document: Document) {
        viewModelScope.launch { filesManager.shareFile(document.fileUri) }
    }

    private fun deleteDocument(document: Document) {
        viewModelScope.launch {
            document.fileUri.path?.let { path ->
                filesManager.deleteFile(File(path))
            }
            document.previewUri.path?.let { path ->
                filesManager.deleteFile(File(path))
            }
            documentsDataManager.delete(document)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            pdfHelper.closeRenderer(renderer)
            renderer = null
        }
    }
}

sealed interface DocumentsEvent {
    data class OnSearchDocuments(val query: String) : DocumentsEvent
    data class OnSortDocuments(val orderState: OrderState) : DocumentsEvent
    data class OnLoadPage(val index: Int) : DocumentsEvent

    data class OnUploadDocument(
        val uri: Uri,
        val name: String,
        val category: DocumentCategory,
    ) : DocumentsEvent


    data class OnShareDocument(val document: Document) : DocumentsEvent
    data class OnDeleteDocument(val document: Document) : DocumentsEvent

    data class OnShowDocument(val document: Document) : DocumentsEvent
    data class OnSearchTextInDocument(val query: String) : DocumentsEvent
    data object OnClearShownDocument : DocumentsEvent
}

sealed interface DocumentsUiState {
    data object Loading : DocumentsUiState

    @Immutable
    data class Success(
        val data: DocumentsData,
        val shownDocument: RenderedDocument?,
    ) : DocumentsUiState
}

@Immutable
data class DocumentsData(
    val orderState: OrderState = OrderState(),
    val searchQuery: String = "",
    val filteredDocuments: List<Document> = emptyList(),
)

@Immutable
data class RenderedDocument(
    val document: Document,
    val pageCount: Int = 0,
    val pageSizes: Map<Int, IntSize> = emptyMap(),
    val pageBitmaps: Map<Int, Bitmap> = emptyMap(),
    val currentPage: Int = 0,
    val search: InDocumentSearchState = InDocumentSearchState(),
)

@Immutable
data class InDocumentSearchState(
    val query: String = "",
    val results: List<DocumentSearchResult> = emptyList(),
)

@Immutable
data class DocumentSearchResult(
    val page: Int,
    val bounds: PageMatchBounds,
)