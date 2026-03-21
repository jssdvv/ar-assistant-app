package com.jssdvv.ara.machines.presentation.destination.markers

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.core.domain.repository.BarcodeWriter
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PDFGeneratorHelper
import com.jssdvv.ara.core.domain.repository.VibratorHelper
import com.jssdvv.ara.machines.domain.model.Document
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.usecase.DocumentsDataManager
import com.jssdvv.ara.machines.domain.usecase.MarkersDataManager
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnClearSelectedItems
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnGetCurrentMarker
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnIndexChange
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnLongPressVibration
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnOpenedMarkerChange
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnSaveMarker
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnSelectItem
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnSelectionModeChange
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnSizeChange
import com.jssdvv.ara.machines.presentation.destination.markers.MarkersEvent.OnUnselectItem
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class MarkersViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val vibratorHelper: VibratorHelper,
    private val barcodeWriter: BarcodeWriter,
    private val pdfGeneratorHelper: PDFGeneratorHelper,
    private val markersDataManager: MarkersDataManager,
    private val documentsDataManager: DocumentsDataManager,
    private val filesManager: FilesManager,
) : ViewModel() {

    val machineId = savedStateHandle.toRoute<MachinesGraph.MarkersRoute>().machineId

    private val markers = MutableStateFlow<List<Marker>>(emptyList())
    private val selectedItems = MutableStateFlow<List<Int>>(emptyList())
    private val isSelectionMode = MutableStateFlow(false)

    // This state flow holds the opened dialog marker id or
    // null if no marker is being edited or created.
    private val currentMarker = MutableStateFlow<Marker?>(null)

    // This state flow holds the opened dialog marker index or
    // null if no marker is being edited or created.
    private val index = MutableStateFlow<Int?>(null)

    // This state flow holds the QR code bitmap associated
    // with the opened marker index or null if index is null.
    private val qrBitmap: StateFlow<Bitmap?> = index
        .debounce(500L)
        .mapLatest { index -> index?.let { withContext(Dispatchers.IO) { generateQrBitmap(it) } } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    // This state flow holds the opened dialog marker size in
    // centimeters or null if no marker is being edited or created.
    private val sizeCentimeters = MutableStateFlow<Float?>(null)

    private var markersJob: Job? = null

    init {
        getMarkers(machineId)
    }

    val uiState: StateFlow<MarkersUiState> = combine(
        markers,
        selectedItems,
        isSelectionMode,
        MarkersUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MarkersUiState.Loading
    )

    val openedMarkerUiState: StateFlow<OpenedMarkerUiState> = combine(
        currentMarker,
        index,
        qrBitmap,
        sizeCentimeters,
        ::OpenedMarkerUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = OpenedMarkerUiState()
    )

    fun onEvent(event: MarkersEvent) {
        when (event) {
            OnLongPressVibration -> onLongPressVibration()
            OnClearSelectedItems -> onClearSelectedItems()
            is OnGetCurrentMarker -> getCurrentMarker(event.id)
            is OnOpenedMarkerChange -> onOpenedMarkerChange(event.marker)
            is OnIndexChange -> onIndexChange(event.index)
            is OnSizeChange -> onSizeChange(event.size)
            is OnSaveMarker -> onSaveMarker()
            MarkersEvent.OnDeleteMarker -> onDeleteMarker()
            is OnSelectItem -> onSelectItem(event.markerIndex)
            is OnUnselectItem -> onUnselectItem(event.markerIndex)
            is OnSelectionModeChange -> onSelectionModeChange(event.isSelectionMode)
            MarkersEvent.OnExportSelectedMarkers -> onExportSelectedMarkers()
        }
    }

    private fun getMarkers(machineId: Int) {
        markersJob?.cancel()
        markersJob = markersDataManager.select(machineId).onEach { markers ->
            this@MarkersViewModel.markers.value = markers
        }.launchIn(viewModelScope)
    }

    private fun onLongPressVibration() = vibratorHelper.longPress()

    private fun onClearSelectedItems() = selectedItems.update { emptyList() }

    private fun onSelectItem(item: Int) = selectedItems.update { it + item }

    private fun onUnselectItem(item: Int) = selectedItems.update { it - item }

    private fun onSelectionModeChange(isSelectionMode: Boolean) {
        this.isSelectionMode.value = isSelectionMode
    }

    private fun onOpenedMarkerChange(marker: Marker?) {
        currentMarker.value = marker
        index.value = marker?.index?.let { index ->
            if (index == 0) findFirstMissingInt(markers.value.map { it.index })
            else index
        }
        sizeCentimeters.value = marker?.sizeCentimeters
    }

    private fun onIndexChange(index: Int) {
        this.index.value = index
    }

    private fun onSizeChange(size: Float) {
        this.sizeCentimeters.value = size
    }

    private fun generateQrBitmap(markerIndex: Int): Bitmap {
        return barcodeWriter.getQRCodeBitmap(
            // Marker code format e. g. M1P1
            text = "M${machineId}P${markerIndex}",

            // The desired side length of the QR code in pixels.
            sideLength = 500
        )
    }

    private fun onSaveMarker() {

        // This gets the marker that is being edited from the database.
        val currentMarker = currentMarker.value

        val openedId = this.currentMarker.value?.id?.coerceAtLeast(0) ?: 0

        // This gets the already stored in database markers indexes.
        val indexes = markers.value.map { it.index }

        // This gets the already stored in database markers indexes
        // without the one that is being edited.
        val markerIndexesWithoutEdited = currentMarker?.let { indexes - it.index } ?: indexes

        // A marker index can't be null or already in the database.
        val openedIndex =
            if (index.value == null || index.value!! in markerIndexesWithoutEdited) {
                findFirstMissingInt(markerIndexesWithoutEdited)
            } else index.value!!

        // A marker size can't be null
        val openedSize = sizeCentimeters.value ?: 9F // Recommended size in centimeters

        when (openedId) {
            // For creation of a new marker
            0 -> {
                viewModelScope.launch {
                    markersDataManager.upsert(
                        Marker(
                            id = 0,
                            machineId = machineId,
                            index = openedIndex,
                            sizeCentimeters = openedSize,
                            imageUri = barcodeWriter.saveBitmapToInternalStorage(
                                machineId = machineId,
                                displayName = "M${machineId}P${openedIndex}",
                                bitmap = qrBitmap.value!!,
                                format = Bitmap.CompressFormat.PNG,
                                quality = 100
                            )
                        )
                    )
                }
            }
            // For update of an existing marker
            else -> {
                viewModelScope.launch {
                    markersDataManager.upsert(
                        Marker(
                            id = openedId,
                            machineId = machineId,
                            index = openedIndex,
                            sizeCentimeters = openedSize,
                            imageUri = barcodeWriter.saveBitmapToInternalStorage(
                                machineId = machineId,
                                displayName = "M${machineId}P${openedIndex}",
                                bitmap = qrBitmap.value!!,
                                format = Bitmap.CompressFormat.PNG,
                                quality = 100
                            )
                        )
                    )
                }
            }
        }
    }

    private fun onDeleteMarker() {
        // This gets the marker that is being edited from the database.
        val currentMarker = currentMarker.value

        viewModelScope.launch {
            if (currentMarker != null) {
                markersDataManager.delete(currentMarker)

                barcodeWriter.deleteBitmapFromInternalStorage(
                    machineId = machineId,
                    displayName = currentMarker.imageUri.path?.split("/")?.last() ?: ""
                )
            }
        }


    }

    private fun getCurrentMarker(id: Int): Marker? =
        markersDataManager.select.selectSingleMarkerById(id)

    private fun onExportSelectedMarkers() {
        val selectedMarkers = markers.value.filterIndexed { index, _ ->
            selectedItems.value.contains(index)
        }

        val markerDocName = "markers.pdf"

        viewModelScope.launch {
            val fileUri = pdfGeneratorHelper.generateMarkersPDF(
                fileName = markerDocName,
                machineId = machineId,
                markers = selectedMarkers
            )

            try {

                documentsDataManager.select.selectDocumentByMachineIdAndName(
                    machineId = machineId,
                    name = markerDocName
                ).let {
                    val document = Document(
                        id = it?.id ?: 0,
                        machineId = machineId,
                        name = markerDocName,
                        fileUri = fileUri
                    )

                    if (it?.id != null) {
                        documentsDataManager.upsert(document)
                    }
                }
            } catch (e: Exception) {
                Log.e("ExportMarkers", "Failed to export markers", e)
            }
        }
    }
}

/**
 * Events that can be triggered by the UI.
 *
 * The Markers UI can trigger the following events on the [MarkersViewModel]:
 *
 * 1. [OnLongPressVibration] - Triggered when the user long presses on an item.
 * 2. [OnClearSelectedItems] - Triggered when the user wants to clear the selection of items.
 * 3. [OnGetCurrentMarker] - Triggered when the user wants to get the current marker from the database.
 * 4. [OnOpenedMarkerChange] - Triggered when the user wants to change the currently opened marker.
 * 5. [OnIndexChange] - Triggered when the user wants to change the index of the currently opened marker.
 * 6. [OnSizeChange] - Triggered when the user wants to change the size of the currently opened marker.
 * 7. [OnSaveMarker] - Triggered when the user wants to save the changes made to the currently opened marker.
 * 8. [OnSelectItem] - Triggered when the user wants to select an item.
 * 9. [OnUnselectItem] - Triggered when the user wants to unselect an item.
 * 10. [OnSelectionModeChange] - Triggered when the user wants to change the selection mode.
 */
sealed class MarkersEvent {
    data object OnLongPressVibration : MarkersEvent()
    data object OnClearSelectedItems : MarkersEvent()
    data class OnGetCurrentMarker(val id: Int) : MarkersEvent()

    /**
     * If the marker is null, there is none currently [Marker] opened.
     */
    data class OnOpenedMarkerChange(val marker: Marker?) : MarkersEvent()
    data class OnIndexChange(val index: Int) : MarkersEvent()
    data class OnSizeChange(val size: Float) : MarkersEvent()
    data object OnSaveMarker : MarkersEvent()
    data object OnDeleteMarker : MarkersEvent()
    data class OnSelectItem(val markerIndex: Int) : MarkersEvent()
    data class OnUnselectItem(val markerIndex: Int) : MarkersEvent()
    data class OnSelectionModeChange(val isSelectionMode: Boolean) : MarkersEvent()
    data object OnExportSelectedMarkers : MarkersEvent()
}

sealed interface MarkersUiState {

    /**
     * State when the markers are being loaded from the database.
     */
    data object Loading : MarkersUiState

    /**
     * State when the markers are successfully loaded from the database.
     *
     * @property [markers] The list of markers.
     * @property [selectedMarkers] The list of selected markers in selection mode.
     * @property [isSelectionMode] Whether the selection mode is active or not.
     */
    data class Success(
        val markers: List<Marker>,
        val selectedMarkers: List<Int>,
        val isSelectionMode: Boolean,
    ) : MarkersUiState
}

/**
 * State of the dialog that shows the QR code of the marker that is being edited or created.
 *
 * @property [currentMarker] The marker that is being edited or created.
 * @property [index] The index of the dialog text field.
 * @property [qrBitmap] The bitmap of the QR code of the marker that is being edited or created.
 * @property [sizeCentimeters] The size of the marker that is being edited or created in the text field.
 */
data class OpenedMarkerUiState(
    val currentMarker: Marker? = null,
    val index: Int? = null,
    val qrBitmap: Bitmap? = null,
    val sizeCentimeters: Float? = 8F,
)

/**
 * Disclaimer: This function is not the most efficient way to find the first missing int,
 * but it works for small lists.
 */
internal fun findFirstMissingInt(integers: List<Int>): Int {
    if (integers.isEmpty()) return 1

    // Loop through the integers until the index
    // is not in the list of integers
    for (index in 1..integers.max() + 1) {
        if (index !in integers) {
            return index
        }
    }
    return 1
}