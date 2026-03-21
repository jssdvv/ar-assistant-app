package com.jssdvv.ara.machines.presentation.destination.ar_session

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.core.domain.repository.BarcodeWriter
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ARSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val barcodeWriter: BarcodeWriter,
) : ViewModel() {

    private val activityId = savedStateHandle.toRoute<MachinesGraph.ARSessionRoute>().activityId
    private val steps = MutableStateFlow<List<Step>>(emptyList())
    private val markers = MutableStateFlow<List<Pair<Marker, Bitmap>>>(emptyList())
    private val currentStep = MutableStateFlow(0)
    private val selectedChip = MutableStateFlow(0)
    private val isBottomSheetOpened = MutableStateFlow(false)

    init {

    }

    val uiState: StateFlow<ARSessionUiState> = combine(
        steps,
        markers,
        currentStep,
        selectedChip,
        isBottomSheetOpened,
        ARSessionUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ARSessionUiState.Loading
    )

    fun onEvent(event: ARSessionEvent) {
        when (event) {
            is ARSessionEvent.OnClickNextStep -> {}
            is ARSessionEvent.OnChipClick -> onChipClick(event.chip)
        }
    }

    private fun onChipClick(chip: Int) {
        selectedChip.value = chip
    }

    private fun getMarkers(machineId: Int) {
//        getMarkersUseCase(machineId).onEach { marker ->
//            this.marker.value = marker.map { marker ->
//                val qrCodeBitmap = generateQRCodeBitmapUseCase(
//                    text = "M${marker.machineId}P${marker.markerIndex}",
//                    sideLength = 300
//                )
//                marker to qrCodeBitmap
//            }
//        }.launchIn(viewModelScope)
    }
}

sealed class ARSessionEvent {
    data class OnClickNextStep(val step: Int) : ARSessionEvent()
    data class OnChipClick(val chip: Int) : ARSessionEvent()
}

sealed interface ARSessionUiState {

    data object Loading : ARSessionUiState

    data class Success(
        val steps: List<Step>,
        val markers: List<Pair<Marker, Bitmap>>,
        val currentStep: Int,
        val selectedChip: Int,
        val isBottomSheetOpened: Boolean,
    ) : ARSessionUiState
}