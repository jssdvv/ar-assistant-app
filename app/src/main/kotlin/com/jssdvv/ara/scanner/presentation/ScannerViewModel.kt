package com.jssdvv.ara.scanner.presentation

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.jssdvv.ara.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Loading)
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun onPermissionsNeeded(context: Context) {
        val preferenceKey =
            UiString.StringResource(R.string.PREFERENCE_CAMERA_REQUEST_PERMISSION_COUNT)
                .asString(context)
        val sharedPreference = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)
        _uiState.value = ScannerUiState.RequestingPermission(
            isPermissionDialogVisible = false,
            permissionRequestCount = sharedPreference.getInt(preferenceKey, 0)
        )
    }

    fun onPermissionsGranted(context: Context) {
        resetCameraRequestPermissionCount(context)
        _uiState.value = ScannerUiState.Success(
            isScanning = true,
            isTorchEnabled = false,
        )
    }

    fun onPermissionResult(result: Boolean, context: Context) {
        if (result) {
            resetCameraRequestPermissionCount(context)
            _uiState.value = ScannerUiState.Success(
                isScanning = true,
                isTorchEnabled = false,
            )
        } else {
            incrementCameraRequestPermissionCount(context)
        }
    }

    private fun incrementCameraRequestPermissionCount(context: Context) {
        val preferenceKey =
            UiString.StringResource(R.string.PREFERENCE_CAMERA_REQUEST_PERMISSION_COUNT)
                .asString(context)
        val sharedPreference = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)
        (uiState.value as? ScannerUiState.RequestingPermission)?.let { uiState ->
            _uiState.value = uiState.copy(
                permissionRequestCount = sharedPreference.incrementCountPreference(preferenceKey)
            )
        }
    }

    private fun resetCameraRequestPermissionCount(context: Context) {
        val preferenceKey =
            UiString.StringResource(R.string.PREFERENCE_CAMERA_REQUEST_PERMISSION_COUNT)
                .asString(context)
        val sharedPreference = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)
        (uiState.value as? ScannerUiState.RequestingPermission)?.let { uiState ->
            _uiState.value = uiState.copy(
                permissionRequestCount = sharedPreference.resetCountPreference(preferenceKey)
            )
        }
    }

    fun setPermissionDialogVisibility(visible: Boolean) {
        (uiState.value as? ScannerUiState.RequestingPermission)?.let { uiState ->
            _uiState.value = uiState.copy(
                isPermissionDialogVisible = visible
            )
        }
    }

    private fun SharedPreferences.incrementCountPreference(key: String): Int {
        val updatedValue = getInt(key, 0).inc()
        this.edit().putInt(key, updatedValue).apply()
        return updatedValue
    }

    private fun SharedPreferences.resetCountPreference(key: String): Int {
        this.edit().putInt(key, 0).apply()
        return 0
    }

    fun toggleTorchState() {
        (uiState.value as? ScannerUiState.Success)?.let { uiState ->
            _uiState.value = uiState.copy(
                isTorchEnabled = !uiState.isTorchEnabled
            )
        }
    }

    fun setScanningState(isScanning: Boolean) {
        (uiState.value as? ScannerUiState.Success)?.let { uiState ->
            _uiState.value = uiState.copy(
                isScanning = isScanning
            )
        }
    }
}

sealed interface ScannerUiState {

    /**
     * Represents the initial state when the scanning feature is first loaded.
     * In this state, the app checks whether the camera permissions have been granted or not.
     * Based on this, it transitions to either the [Success] state if permissions are granted,
     * or to the [RequestingPermission] state if permissions are needed.
     */
    data object Loading : ScannerUiState

    /**
     * Represents the state where the app is requesting camera permissions from the user.
     * This state is triggered when the app needs the camera permissions to function.
     *
     * Camera permissions are requested up to twice. If the user declines both times,
     * they must manually grant permissions in the system settings to proceed with scanning.
     *
     * @property isPermissionDialogVisible Indicates whether the permission dialog is currently
     * visible to the user. If true, the dialog is displayed; if false, it is not shown.
     * @property permissionRequestCount The number of times the permission has been requested
     * from the user. This value is incremented each time the app requests camera permissions.
     */
    data class RequestingPermission(
        val isPermissionDialogVisible: Boolean,
        val permissionRequestCount: Int
    ) : ScannerUiState

    /**
     * Represents the state when the camera permissions have been successfully granted,
     * and the user can start using the scanning functionality.
     *
     * In this state, the screen displays various UI controls to manage the camera, such as
     * enabling/disabling the torch (flashlight), flipping the camera (front/back), and
     * controlling whether the scanning is active or paused.
     *
     * @property isScanning Boolean flag indicating whether the scanning process is currently active.
     * @property isTorchEnabled Boolean flag indicating whether the torch (flashlight) is turned on.
     */
    data class Success(
        val isScanning: Boolean,
        val isTorchEnabled: Boolean,
    ) : ScannerUiState
}

sealed class UiString {
    abstract fun asString(context: Context): String

    class StringResource(
        @StringRes val resId: Int,
        private vararg val args: Any
    ) : UiString() {
        override fun asString(context: Context): String {
            return context.getString(resId, *args)
        }
    }
}
