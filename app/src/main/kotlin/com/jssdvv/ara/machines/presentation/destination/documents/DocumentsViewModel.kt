package com.jssdvv.ara.machines.presentation.destination.documents

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Document
import com.jssdvv.ara.machines.domain.model.DocumentCategory
import com.jssdvv.ara.machines.domain.type.OrderKey
import com.jssdvv.ara.machines.domain.usecase.DocumentsDataManager
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val documentsDataManager: DocumentsDataManager,
    private val filesManager: FilesManager,
) : ViewModel() {

    companion object {
        const val PRELOAD_NEXT_ROWS = 4
        const val RETAIN_PREVIOUS_ROWS = 4
    }

    val machineId: Int = savedStateHandle.toRoute<MachinesGraph.DocumentsRoute>().machineId

    private val query = MutableStateFlow("")
    private val orderType = MutableStateFlow(OrderType.ASCENDING)
    private val orderKey = MutableStateFlow(OrderKey.NAME)

    private val shownDocument = MutableStateFlow<Document?>(null)
    private val documents: StateFlow<List<Document>> = documentsDataManager
        .select(machineId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val filteredDocuments: StateFlow<List<Document>> = combine(
        documents,
        query.debounce(500L),
        orderType,
        orderKey
    ) { documents, query, orderType, orderKey ->
        documents
            .filter { it.name.contains(query, ignoreCase = true) }
            .let { filtered ->
                when (orderType) {
                    OrderType.ASCENDING -> when (orderKey) {
                        OrderKey.NAME -> filtered.sortedBy { it.name.lowercase() }
                        OrderKey.TYPE -> filtered.sortedBy { it.category }
                        else -> filtered.sortedBy { it.createdAt }
                    }

                    OrderType.DESCENDING -> when (orderKey) {
                        OrderKey.NAME -> filtered.sortedByDescending { it.name.lowercase() }
                        OrderKey.TYPE -> filtered.sortedByDescending { it.category }
                        else -> filtered.sortedByDescending { it.createdAt }
                    }
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    val uiState: StateFlow<DocumentsUiState> = combine(
        orderType,
        orderKey,
        query,
        filteredDocuments,
        shownDocument,
        DocumentsUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DocumentsUiState.Loading
    )

    fun onEvent(event: DocumentsEvent) {
        when (event) {
            is DocumentsEvent.OnShowDocument -> showDocument(event.document)
            is DocumentsEvent.OnSearchDocuments -> query.value = event.query
            is DocumentsEvent.OnSortDocuments -> sortDocuments(event.orderType, event.orderKey)
            is DocumentsEvent.OnUploadDocument -> uploadDocument(
                event.uri,
                event.name,
                event.category
            )

            is DocumentsEvent.OnDeleteDocument -> deleteDocument(event.document)
            is DocumentsEvent.OnClearShownDocument -> shownDocument.value = null
        }
    }

    private fun sortDocuments(orderType: OrderType, orderKey: OrderKey) {
        this.orderType.value = orderType
        this.orderKey.value = orderKey
    }

    private fun showDocument(document: Document) {
        shownDocument.value = document
    }

    private fun uploadDocument(uri: Uri, name: String, category: DocumentCategory) {
        viewModelScope.launch {
            val pdfFile = filesManager.copyPdfToInternalStorage(uri, machineId) ?: return@launch

            val document = Document(
                machineId = machineId,
                category = category,
                name = name,
                fileUri = Uri.fromFile(pdfFile),
                previewUri = Uri.EMPTY,
                createdAt = Date()
            )

            documentsDataManager.upsert(document)
        }
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
}

sealed interface DocumentsUiState {
    data object Loading : DocumentsUiState
    data class Success(
        val orderType: OrderType,
        val orderKey: OrderKey,
        val searchQuery: String = String(),
        val filteredDocuments: List<Document> = emptyList(),
        val shownDocument: Document? = null,
    ) : DocumentsUiState
}

sealed interface DocumentsEvent {
    data class OnShowDocument(val document: Document) : DocumentsEvent
    data class OnSearchDocuments(val query: String) : DocumentsEvent
    data class OnSortDocuments(
        val orderType: OrderType,
        val orderKey: OrderKey
    ) : DocumentsEvent

    data class OnUploadDocument(
        val uri: Uri,
        val name: String,
        val category: DocumentCategory,
    ) : DocumentsEvent

    data class OnDeleteDocument(val document: Document) : DocumentsEvent
    data object OnClearShownDocument : DocumentsEvent
}