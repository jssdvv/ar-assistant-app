package com.jssdvv.ara.machines.presentation.destination.documents

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.type.OrderKey
import com.jssdvv.ara.core.presentation.common.component.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.foundation.component.OrderSection
import com.jssdvv.ara.core.presentation.foundation.component.SearchTopBar
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Document
import com.jssdvv.ara.machines.presentation.destination.documents.component.DocumentCreationDialog
import com.jssdvv.ara.machines.presentation.destination.documents.component.DocumentDeletionDialog
import com.jssdvv.ara.machines.presentation.destination.documents.component.DocumentFAB
import com.jssdvv.ara.machines.presentation.destination.documents.component.DocumentVisor
import com.jssdvv.ara.machines.presentation.destination.documents.component.MiniDocumentPreviewCard

@Composable
fun DocumentsDestination(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: DocumentsViewModel = hiltViewModel()
) {
    DocumentsScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
internal fun DocumentsScreen(
    uiState: DocumentsUiState,
    onEvent: (DocumentsEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        DocumentsUiState.Loading -> LoadingWheelScreen()
        is DocumentsUiState.Success -> DocumentsContent(
            data = uiState.data,
            openedDoc = uiState.shownDocument,
            onEvent = onEvent,
            onNavigateUp = onNavigateBack,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsContent(
    data: DocumentsData,
    openedDoc: RenderedDocument?,
    onEvent: (DocumentsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreationDialog by remember { mutableStateOf(false) }
    var selectedUriForCreation by remember { mutableStateOf<Uri?>(null) }

    var showDeletionDialog by remember { mutableStateOf(false) }
    var documentToDelete by remember { mutableStateOf<Document?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SearchTopBar(
                value = data.searchQuery,
                onValueChange = { onEvent(DocumentsEvent.OnSearchDocuments(it)) },
                placeholder = { Text(stringResource(R.string.search_bar_documents_supporting_text)) },
                navigationIcon = { NavigationUpIconButton(onNavigateUp) },
                bottomRow = {
                    OrderSection(
                        orderState = data.orderState,
                        usedOrderKeys = listOf(OrderKey.NAME, OrderKey.TYPE, OrderKey.CREATION_DATE),
                        onChangeOrder = { onEvent(DocumentsEvent.OnSortDocuments(it)) }
                    )
                }
            )
        },
        floatingActionButton = {
            DocumentFAB(
                onPdfSelected = { uri ->
                    selectedUriForCreation = uri
                    showCreationDialog = true
                }
            )
        }
    ) { paddingValues ->
        if (data.filteredDocuments.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.screen_documents_empty_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(top = MaterialTheme.spacing.small, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                items(
                    items = data.filteredDocuments,
                    key = { it.id }
                ) { document ->
                    MiniDocumentPreviewCard(
                        document = document,
                        onClick = { onEvent(DocumentsEvent.OnShowDocument(document)) },
                        onDeleteClick = { onEvent(DocumentsEvent.OnDeleteDocument(document)) }
                    )
                }
            }
        }
    }

    if (showCreationDialog && selectedUriForCreation != null) {
        val fileName = selectedUriForCreation.toString()
            .substringAfterLast('/')
            .substringBeforeLast('.')

        DocumentCreationDialog(
            initialName = fileName,
            onSave = { name, category ->
                onEvent(
                    DocumentsEvent.OnUploadDocument(
                        uri = selectedUriForCreation!!,
                        name = name,
                        category = category
                    )
                )
                showCreationDialog = false
                selectedUriForCreation = null
            },
            onDismiss = {
                showCreationDialog = false
                selectedUriForCreation = null
            }
        )
    }

    if (showDeletionDialog && documentToDelete != null) {
        DocumentDeletionDialog(
            onConfirm = {
                onEvent(DocumentsEvent.OnDeleteDocument(documentToDelete!!))
                showDeletionDialog = false
                documentToDelete = null
            },
            onDismiss = {
                showDeletionDialog = false
                documentToDelete = null
            }
        )
    }

    if(openedDoc != null) {
        DocumentVisor(
            document = openedDoc.document,
            pageCount = openedDoc.pageCount,
            pageSizes = openedDoc   .pageSizes,
            pageBitmaps = openedDoc.pageBitmaps,
            currentPage = openedDoc.currentPage,
            search = openedDoc.search,
            onLoadPage = {onEvent(DocumentsEvent.OnLoadPage(it))},
            onShareDocument = { onEvent(DocumentsEvent.OnShareDocument(it)) },
            onCloseDocument = { onEvent(DocumentsEvent.OnClearShownDocument) }
        )
    }
}