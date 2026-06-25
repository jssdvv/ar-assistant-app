package com.jssdvv.ara.machines.presentation.destination.markers

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.AddIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.presentation.destination.markers.component.MarkerDialog
import com.jssdvv.ara.machines.presentation.destination.markers.component.MarkersTopBar
import com.jssdvv.ara.machines.presentation.destination.markers.component.SelectableItem

@Composable
fun MarkersDestination(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: MarkersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogUiState by viewModel.openedMarkerUiState.collectAsStateWithLifecycle()
    MarkersScreen(
        modifier = modifier,
        machineId = viewModel.machineId,
        uiState = uiState,
        openedMarkerUiState = dialogUiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun MarkersScreen(
    modifier: Modifier = Modifier,
    machineId: Int,
    uiState: MarkersUiState,
    openedMarkerUiState: OpenedMarkerUiState,
    onEvent: (MarkersEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val successUiState = uiState as? MarkersUiState.Success
    val isSelectionMode = successUiState?.isSelectionMode ?: false
    var showDeleteMarkersDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            MarkersTopBar(
                onNavigateBack = onNavigateBack,
                isSelectionMode = successUiState?.isSelectionMode ?: false,
                onSelectionModeChange = { onEvent(MarkersEvent.OnSelectionModeChange(it)) },
                onClearSelectedItems = { onEvent(MarkersEvent.OnClearSelectedItems) },
                selectedCountItems = successUiState?.selectedMarkers?.size ?: 0,
                onExportSelectedMarkers = { onEvent(MarkersEvent.OnExportSelectedMarkers) },
                onDeleteSelectedMarkers = { showDeleteMarkersDialog = true },
            )
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        onEvent(
                            MarkersEvent.OnOpenedMarkerChange(Marker(machineId = machineId))
                        )
                    },
                    icon = { AddIcon() },
                    text = { Text(stringResource(R.string.fab_markers_create_action)) }
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            MarkersUiState.Loading -> {
                LoadingWheelScreen(
                    modifier = modifier.padding(paddingValues)
                )
            }

            is MarkersUiState.Success -> {
                MarkersSuccessScreen(
                    modifier = modifier.padding(paddingValues),
                    machineId = machineId,
                    markers = uiState.markers,
                    openedMarkerUiState = openedMarkerUiState,
                    selectedItems = uiState.selectedMarkers,
                    isSelectionMode = uiState.isSelectionMode,
                    showDeleteMarkersDialog = showDeleteMarkersDialog,
                    onShowDeleteMarkerDialog = {showDeleteMarkersDialog = it},
                    onEvent = onEvent,
                )
            }
        }
    }
}

@Composable
fun MarkersSuccessScreen(
    modifier: Modifier = Modifier,
    machineId: Int,
    markers: List<Marker>,
    openedMarkerUiState: OpenedMarkerUiState,
    selectedItems: List<Int>,
    isSelectionMode: Boolean,
    showDeleteMarkersDialog: Boolean,
    onShowDeleteMarkerDialog: (Boolean) -> Unit,
    onEvent: (MarkersEvent) -> Unit,
) {
    BackHandler(enabled = isSelectionMode) {
        onEvent(MarkersEvent.OnSelectionModeChange(false))
        onEvent(MarkersEvent.OnClearSelectedItems)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        itemsIndexed(markers) { index, marker ->
            SelectableItem(
                modifier = Modifier,
                machineId = machineId,
                markerIndex = marker.index,
                markerSize = marker.sizeCentimeters,
                isSelectionMode = isSelectionMode,
                isSelected = selectedItems.contains(index),
                onClick = {
                    onEvent(MarkersEvent.OnOpenedMarkerChange(marker))
                },
                onLongPress = { isSelected ->
                    if (!isSelectionMode) {
                        onEvent(MarkersEvent.OnLongPressVibration)
                        onEvent(MarkersEvent.OnSelectionModeChange(true))
                    }
                    if (!isSelected) {
                        onEvent(MarkersEvent.OnSelectItem(index))
                    } else {
                        onEvent(MarkersEvent.OnUnselectItem(index))
                    }
                },
                onSelectionChange = { isSelected ->
                    if (isSelected) {
                        onEvent(MarkersEvent.OnSelectItem(index))
                    } else {
                        onEvent(MarkersEvent.OnUnselectItem(index))
                    }
                }
            )
        }
    }

    if (openedMarkerUiState.currentMarker != null) {
        MarkerDialog(
            currentMarker = openedMarkerUiState.currentMarker,
            index = openedMarkerUiState.index,
            qrBitmap = openedMarkerUiState.qrBitmap,
            sizeCentimeters = openedMarkerUiState.sizeCentimeters,
            usedMarkerIndexes = markers.map { it.index },
            onDismissRequest = { onEvent(MarkersEvent.OnOpenedMarkerChange(null)) },
            onIndexChange = { onEvent(MarkersEvent.OnIndexChange(it)) },
            onSizeChange = { onEvent(MarkersEvent.OnSizeChange(it)) },
            onSaveMarker = { onEvent(MarkersEvent.OnSaveMarker) },
            onDeleteMarker = {
                onEvent(MarkersEvent.OnDeleteMarker)
                onEvent(MarkersEvent.OnOpenedMarkerChange(null))
                             },
        )
    }

    if (showDeleteMarkersDialog) {
        AlertDialog(
            onDismissRequest = { onShowDeleteMarkerDialog(false) },
            title = {
                Text(text = stringResource(R.string.dialog_delete_markers_title))
            },
            text = {
                Text(text = stringResource(R.string.dialog_delete_markers_warning))
            },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(MarkersEvent.OnDeleteSelectedMarkers)
                    onShowDeleteMarkerDialog(false)
                }) {
                    Text(text = stringResource(R.string.button_delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDeleteMarkerDialog(false) }) {
                    Text(text = stringResource(R.string.button_cancel_action))
                }
            }
        )
    }
}