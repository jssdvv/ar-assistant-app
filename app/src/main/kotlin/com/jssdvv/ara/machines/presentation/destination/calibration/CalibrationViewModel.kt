package com.jssdvv.ara.machines.presentation.destination.calibration

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PermissionHandler
import com.jssdvv.ara.core.domain.utility.PermissionState
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.usecase.MarkersDataManager
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModelsCalibrationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val permissionHandler: PermissionHandler,
    private val filesManager: FilesManager,
    private val markersDataManager: MarkersDataManager,
    private val modelsDataManager: ModelsDataManager,
) : ViewModel() {

    companion object {

        private val isMinSdk33 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        private val isMinSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val permissions = when {
            isMinSdk33 -> arrayOf(
                Manifest.permission.CAMERA
            )

            isMinSdk29 -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )

            else -> arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
    }

    val machineId = savedStateHandle.toRoute<MachinesGraph.CalibrationRoute>().machineId

    // Permissions Pair<Permission string, Permission state>
    private val _permissionsStates = MutableStateFlow(emptyList<Pair<String, PermissionState>>())

    // State holders for data from the database
    private val markers = MutableStateFlow(emptyList<Marker>())
    private val models = MutableStateFlow(emptyList<Model>())

    // State holders for marker selection
    private val selectedMarker = MutableStateFlow<Marker?>(null)
    private val selectedMarkerBitmap: StateFlow<BitmapInfo?> = selectedMarker
        .mapNotNull { it?.let { marker -> marker.id to marker.imageUri } }
        .distinctUntilChanged()
        .mapLatest { (id, imageUri) ->
            withContext(Dispatchers.IO) {
                filesManager.getBitmapFromInputStream(filesManager.getInputStreamFromUri(imageUri))
            }?.let { bitmap ->
                BitmapInfo(id, bitmap)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            null
        )

    init {
        val permissionsPairs = permissions.map { it to true }
        onCheckPermissionsStates(permissionsPairs)
        getMarkers(machineId)
        getModels(machineId)
    }

    val permissionsStates = _permissionsStates.asStateFlow()

    val uiState: StateFlow<ModelsCalibrationUiState> = combine(
        markers,
        models,
        selectedMarker,
        selectedMarkerBitmap,
        ModelsCalibrationUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ModelsCalibrationUiState.Loading
    )

    fun onEvent(event: ModelsEvent) {
        when (event) {
            is ModelsEvent.OnCheckPermissionsStates -> onCheckPermissionsStates(event.permissions)
            is ModelsEvent.OnPermissionInteraction -> onPermissionInteraction(event.permission)

            is ModelsEvent.OnSelectMarker -> selectedMarker.update { event.marker }

            is ModelsEvent.OnCalibrateOriginToMarker -> calibrateOriginToMarker(
                event.markerId,
                event.newOriginPosition,
                event.newOriginQuaternion
            )

            is ModelsEvent.OnCalibrateModelsToOrigin -> calibrateSelectedModelToOrigin(
                event.modelId,
                event.newModelPosition,
                event.newModelQuaternion
            )

            is ModelsEvent.OnInsertModel -> insertModel(event.contentUri)
            ModelsEvent.OnDeleteModel -> deleteSelectedModel()
        }
    }


    /**
     * This method checks the permissions states and updates the [_permissionsStates] value.
     *
     * @param [permissions] List of pairs of <Permission string, Permission state>
     */
    private fun onCheckPermissionsStates(
        permissions: List<Pair<String, Boolean>>,
    ) {
        _permissionsStates.value = permissions.map {
            it.first to permissionHandler.getPermissionState(it.first, it.second)
        }
    }

    private fun onPermissionInteraction(permission: String) =
        permissionHandler.onPermissionDialogInteraction(permission)

    private fun getMarkers(machineId: Int) =
        markersDataManager.select(machineId).onEach { markers.value = it }.launchIn(viewModelScope)

    private fun getModels(machineId: Int) =
        modelsDataManager.select(machineId).onEach { models.value = it }
            .launchIn(viewModelScope)

    private fun calibrateOriginToMarker(
        markerId: Int,
        newOriginPosition: Position,
        newOriginQuaternion: Quaternion,
    ) {
        val markerToUpdate = markers.value.find { it.id == markerId }?.copy(
            calibrated = true,
            originPosition = newOriginPosition,
            originRotation = newOriginQuaternion
        ) ?: return

        viewModelScope.launch {
            markersDataManager.upsert(markerToUpdate)
        }
    }

    private fun calibrateSelectedModelToOrigin(
        modelId: Int,
        newModelPosition: Position,
        newModelQuaternion: Quaternion
    ) {
        val modelToUpdate = models.value.find { it.id == modelId }?.copy(
            calibrated = true,
            positionFromOrigin = newModelPosition,
            rotationFromOrigin = newModelQuaternion
        )

        if (modelToUpdate == null) return

        viewModelScope.launch {
            modelsDataManager.upsert.upsertModels(modelToUpdate)
        }
    }

    /**
     * This method inserts a new model in the database which is then used
     * in the ui by a launched effect.
     */
    private fun insertModel(contentUri: Uri) {

        val modelFile = filesManager.copyModelToInternalStorage(
            contentUri = contentUri,
            machineId = machineId,
        )

        if (modelFile == null) return

        val fileName = contentUri.path?.substringAfterLast('/') ?: "model"
        val displayName = fileName
            .substringBeforeLast('.')
            .replace('_', ' ')
            .replace('-', ' ')
            .replace('(', ' ')
            .replace(')', ' ')
            .replace('.', ' ')
            .replaceFirstChar { it.uppercaseChar() }
            .trim()

        val existingModelNames = models.value.map { it.name }

        var finalName = displayName
        var i = 1
        while (finalName in existingModelNames) {
            finalName = "$displayName (${++i})"
        }

        val modelToInsert = Model(
            machineId = machineId,
            name = finalName,
            glbUri = modelFile.toUri()
        )

        viewModelScope.launch { modelsDataManager.upsert.upsertModels(modelToInsert) }
    }

    // TODO: delete from internal storage the model
    // 1. Fix implemented maybe
    private fun deleteSelectedModel() {
        //val currentModelPosition = currentModelPose.value ?: return

//        val modelToDelete = models.value.find { it.id == currentModelPosition.id } ?: return
//        viewModelScope.launch { modelsDataManager.delete.deleteModels(modelToDelete) }
    }
}

sealed interface ModelsEvent {
    data class OnCheckPermissionsStates(val permissions: List<Pair<String, Boolean>>) : ModelsEvent
    data class OnPermissionInteraction(val permission: String) : ModelsEvent
    data class OnSelectMarker(val marker: Marker) : ModelsEvent
    data class OnCalibrateOriginToMarker(
        val markerId: Int,
        val newOriginPosition: Position,
        val newOriginQuaternion: Quaternion
    ) : ModelsEvent

    data class OnCalibrateModelsToOrigin(
        val modelId: Int,
        val newModelPosition: Position,
        val newModelQuaternion: Quaternion
    ) : ModelsEvent

    data class OnInsertModel(val contentUri: Uri) : ModelsEvent
    data object OnDeleteModel : ModelsEvent
}

sealed interface ModelsCalibrationUiState {

    data object Loading : ModelsCalibrationUiState

    /**
     * Data class containing the successfully loaded data of the models screen from
     * local database.
     *
     * @property [markers] the loaded markers from the current machine.
     * @property [models] the loaded models from the current machine that are modelNodes.
     * @property [selectedMarker] the current selected marker.
     * @property [selectedMarkerBitmap] the current selected marker bitmap.
     */
    data class Success(
        val markers: List<Marker>,
        val models: List<Model>,
        val selectedMarker: Marker? = null,
        val selectedMarkerBitmap: BitmapInfo? = null,
    ) : ModelsCalibrationUiState
}

data class BitmapInfo(
    val markerId: Int,
    val bitmap: Bitmap
)