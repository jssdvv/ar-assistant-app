package com.jssdvv.ara.scanner.presentation.destination.scanner

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.domain.repository.PermissionHandler
import com.jssdvv.ara.core.domain.utility.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val permissionHandler: PermissionHandler,
) : ViewModel() {

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

    private val _cameraPermissionState = MutableStateFlow<PermissionState>(PermissionState.Denied())
    private val isScanning = MutableStateFlow(true)
    private val isTorchEnabled = MutableStateFlow(false)

    init {
        onCheckPermissionState(CAMERA_PERMISSION, true)
    }

    // Camera permissions are requested up to twice. If the user decline in the second time,
    // it must manually grant permissions in the system settings to proceed with scanning.
    val cameraPermissionState = _cameraPermissionState.asStateFlow()

    val uiState: StateFlow<ScannerUiState> = combine(
        isScanning,
        isTorchEnabled,
        ScannerUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScannerUiState.Loading
    )

    fun onEvent(event: ScannerEvent) {
        when (event) {
            is ScannerEvent.OnCheckPermissionState -> onCheckPermissionState(
                event.permission,
                event.shouldShowDialog
            )

            is ScannerEvent.OnPermissionInteraction -> onPermissionInteraction(event.permission)
            is ScannerEvent.OnSetScanningState -> isScanning.update { event.isScanning }
            is ScannerEvent.OnToggleTorch -> isTorchEnabled.update { !it }
            is ScannerEvent.OnSetTorchState -> isTorchEnabled.update { event.isTorchEnabled }
        }
    }

    private fun onCheckPermissionState(permission: String, shouldShowDialog: Boolean) {
        _cameraPermissionState.value =
            permissionHandler.getPermissionState(permission, shouldShowDialog)
    }

    private fun onPermissionInteraction(permission: String) =
        permissionHandler.onPermissionDialogInteraction(permission)

}

sealed class ScannerEvent {
    data class OnCheckPermissionState(
        val permission: String,
        val shouldShowDialog: Boolean = false,
    ) : ScannerEvent()

    data class OnPermissionInteraction(val permission: String) : ScannerEvent()
    data class OnSetScanningState(val isScanning: Boolean) : ScannerEvent()
    data object OnToggleTorch : ScannerEvent()
    data class OnSetTorchState(val isTorchEnabled: Boolean) : ScannerEvent()
}

sealed interface ScannerUiState {

    /**
     * Represents the state after the user has granted required permissions. This shows a loading
     * indicator and then navigates to the [Success] state.
     */
    data object Loading : ScannerUiState

    /**
     * Represents the state when the camera permissions have been successfully granted,
     * and the user can start using the scanning functionality.
     *
     * In this state, the screen displays various UI controls to manage the camera, such as enabling
     * or disabling the torch (flashlight) and controlling whether the scanning is active or paused.
     *
     * @property [isScanning] Boolean flag indicating whether the scanning process is currently active.
     * @property [isTorchEnabled] Boolean flag indicating whether the torch (flashlight) is turned on.
     */
    data class Success(
        val isScanning: Boolean = true,
        val isTorchEnabled: Boolean = false,
    ) : ScannerUiState
}