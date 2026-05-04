package com.jssdvv.ara.machines.presentation.destination.calibration

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PermissionHandler
import com.jssdvv.ara.core.presentation.common.state.ManifestString
import com.jssdvv.ara.core.presentation.common.state.Permission
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.measurement.Measurement
import com.jssdvv.ara.machines.domain.type.measurement.MeasurementMode
import com.jssdvv.ara.machines.domain.usecase.MarkersDataManager
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.presentation.destination.ar_session.NotificationEvent
import com.jssdvv.ara.machines.presentation.destination.steps.functions.unidirectionalRotation
import com.jssdvv.ara.machines.presentation.destination.steps.functions.unidirectionalTranslation
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import com.jssdvv.ara.machines.presentation.sceneview.node.ContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.MarkerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.OriginNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.applyOffset
import com.jssdvv.ara.machines.presentation.sceneview.utility.setUnselectedMaterial
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Transform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModelsCalibrationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val permissionHandler: PermissionHandler,
    private val filesManager: FilesManager,
    private val markersDataManager: MarkersDataManager,
    private val modelsDataManager: ModelsDataManager,
) : ViewModel() {

    companion object {
        private val isMinSdk33 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        private val isMinSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val permissionsManifestStrings = when {
            isMinSdk33 -> arrayOf<ManifestString>(
                Manifest.permission.CAMERA
            )

            isMinSdk29 -> arrayOf<ManifestString>(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )

            else -> arrayOf<ManifestString>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
    }

    val machineId = savedStateHandle.toRoute<MachinesGraph.CalibrationRoute>().machineId

    private val _permissions = MutableStateFlow<Set<Permission>>(
        permissionsManifestStrings.mapTo(mutableSetOf()) { manifestString ->
            Permission(
                manifestString = manifestString,
                state = permissionHandler.getPermissionState(manifestString, true)
            )
        }
    )

    private val _notification = MutableSharedFlow<NotificationEvent>(extraBufferCapacity = 1)

    private val markers: StateFlow<List<Marker>> = markersDataManager
        .select(machineId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val models: StateFlow<List<Model>> = modelsDataManager
        .select(machineId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val data: StateFlow<CalibrationData> = combine(
        markers,
        models,
        ::CalibrationData
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CalibrationData()
    )

    private val previousModelTransform = MutableStateFlow(Transform())
    private val selectedContainerId = MutableStateFlow<Int?>(null)
    private val selectedMeasurement = MutableStateFlow(Measurement.TRANSLATION)
    private val selectedMode = MutableStateFlow(MeasurementMode())
    private val selectedMarker = MutableStateFlow<Marker?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentBitmapInfo: StateFlow<BitmapInfo?> = selectedMarker
        .mapNotNull { it?.let { marker -> marker.id to marker.imageUri } }
        .distinctUntilChanged()
        .mapLatest { (id, imageUri) ->
            filesManager.getBitmap(imageUri)?.let { BitmapInfo(id, it) }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val items: StateFlow<CalibrationItems> = combine(
        selectedContainerId,
        selectedMeasurement,
        selectedMode,
        selectedMarker,
        currentBitmapInfo,
        ::CalibrationItems
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CalibrationItems()
    )

    private val options = MutableStateFlow(CalibrationOptions())

    val permissions = _permissions.asStateFlow()
    val notification = _notification.asSharedFlow()
    val uiState: StateFlow<ModelsCalibrationUiState> = combine(
        data,
        items,
        options
    ) { data, items, options ->
        if (data.markers.isEmpty()) {
            ModelsCalibrationUiState.EmptyMarkers
        } else {
            ModelsCalibrationUiState.Success(data, items, options)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ModelsCalibrationUiState.Loading
    )

    fun onEvent(event: ModelsEvent) {
        when (event) {
            is ModelsEvent.OnCheckPermissionsStates -> onCheckPermissionsStates()
            is ModelsEvent.OnPermissionInteraction -> onPermissionInteraction(event.manifestString)
            is ModelsEvent.OnSelectMarker -> selectedMarker.update { event.marker }

            is ModelsEvent.OnRepositionOrigin -> repositionOrigin(
                event.originNode,
                event.markerNode
            )

            is ModelsEvent.OnCalibrateOriginToMarker -> calibrateOriginToMarker(
                event.originNode,
                event.markerNode
            )

            is ModelsEvent.OnSelectContainer -> selectContainer(
                event.containerNode,
                event.materialLoader
            )

            is ModelsEvent.OnCancelContainerCalibration -> cancelContainerCalibration(
                event.containerNode,
                event.materialLoader
            )

            is ModelsEvent.OnCalibrateContainerToOrigin -> calibrateContainerToOrigin(
                event.containerNode,
                event.materialLoader
            )

            is ModelsEvent.OnInsertModel -> insertModel(event.contentUri)
            ModelsEvent.OnDeleteModel -> deleteSelectedModel()

            is ModelsEvent.OnChangeMeasurement -> selectedMeasurement.update { event.measurement }
            is ModelsEvent.OnChangeMeasurementMode -> selectedMode.update { event.mode }

            is ModelsEvent.OnRestoreContainerDefaults -> restoreContainerDefaults(event.container)
            is ModelsEvent.OnTickDragged -> tickDragged(event.container, event.axis, event.tick)

            ModelsEvent.OnToggleTorch -> toggleTorch()
            ModelsEvent.OnTogglePlane -> togglePlane()
        }
    }

    private fun onCheckPermissionsStates() {
        this._permissions.update {
            permissionsManifestStrings.mapTo(mutableSetOf()) { string ->
                Permission(
                    manifestString = string,
                    state = permissionHandler.getPermissionState(string, true)
                )
            }
        }
    }

    private fun onPermissionInteraction(permission: ManifestString) {
        permissionHandler.onPermissionDialogInteraction(permission)
    }

    private fun repositionOrigin(originNode: OriginNode?, markerNode: MarkerNode?) {
        originNode?.repositionToMarker(markerNode, selectedMarker.value?.originOffsetTransform)
        val positionNumber = selectedMarker.value?.index ?: return
        emitNotification(
            NotificationEvent(
                message = R.string.notification_chip_message_origin_repositioned,
                args = listOf("M${machineId}P${positionNumber}")
            )
        )
    }

    private fun calibrateOriginToMarker(originNode: OriginNode?, markerNode: MarkerNode?) {
        if (markerNode == null || originNode == null) return
        val currentMarker = selectedMarker.value ?: return
        val markerToUpdate = markers.value.find { it.id == currentMarker.id }?.copy(
            calibrated = true,
            originOffsetTransform = markerNode.getLocalTransform(originNode.transform)
        ) ?: return
        val positionNumber = currentMarker.index
        emitNotification(
            NotificationEvent(
                message = R.string.notification_chip_message_origin_calibrated,
                args = listOf("M${machineId}P${positionNumber}")
            )
        )

        viewModelScope.launch { markersDataManager.upsert(markerToUpdate) }
    }

    private fun selectContainer(
        containerNode: ContainerNode,
        materialLoader: MaterialLoader
    ) {
        containerNode.apply {
            selectedContainerId.value = modelId
            previousModelTransform.value = transform
            setGizmoVisibility(true, materialLoader)
        }
    }

    private fun calibrateContainerToOrigin(
        containerNode: ContainerNode?,
        materialLoader: MaterialLoader
    ) {
        viewModelScope.launch {
            containerNode?.apply {
                modelNode?.setUnselectedMaterial(materialLoader)
                setGizmoVisibility(false, materialLoader)

                val modelToUpdate = models.value.find { it.id == this.modelId }?.copy(
                    calibrated = true,
                    offsetTransform = containerNode.transform
                ) ?: return@apply

                modelsDataManager.upsert.upsertModels(modelToUpdate)
                val positionNumber = selectedMarker.value?.index ?: return@apply
                emitNotification(
                    NotificationEvent(
                        message = R.string.notification_chip_message_model_calibrated,
                        args = listOf(modelToUpdate.name, "M${machineId}P${positionNumber}")
                    )
                )
            }
            selectedContainerId.value = null
        }
    }

    private fun cancelContainerCalibration(
        containerNode: ContainerNode?,
        materialLoader: MaterialLoader
    ) {
        viewModelScope.launch {
            containerNode?.apply {
                transform = previousModelTransform.value
                modelNode?.setUnselectedMaterial(materialLoader)
                setGizmoVisibility(false, materialLoader)
            }
            selectedContainerId.value = null
        }
    }

    /**
     * This method inserts a new model in the database which is then used
     * in the ui by a launched effect.
     */
    private fun insertModel(contentUri: Uri) {

        val modelFile = filesManager.copyModelToInternalStorage(
            uri = contentUri,
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

    private fun deleteSelectedModel() {
        val currentModel = models.value.find { it.id == selectedContainerId.value } ?: return
        viewModelScope.launch { modelsDataManager.delete.deleteModels(currentModel) }
    }

    private fun restoreContainerDefaults(container: ContainerNode?) {
        container?.apply {
            if (selectedMeasurement.value == Measurement.TRANSLATION) {
                restorePosition()
            } else {
                restoreQuaternion()
            }
        }
    }

    private fun tickDragged(container: ContainerNode?, axis: Axis, tick: Int) {
        container?.apply {
            if (selectedMeasurement.value == Measurement.TRANSLATION) {
                val millis = selectedMode.value.translation.millisPerUnit * tick / 1000F
                val offset = unidirectionalTranslation(axis, millis)
                applyOffset(offset)
            } else {
                val degrees = selectedMode.value.rotation.halfDegreesPerUnit * tick / 2F
                val offset = unidirectionalRotation(axis, degrees)
                applyOffset(offset)
            }
        }
    }

    private fun toggleTorch() {
        options.update { it.copy(torchEnabled = !it.torchEnabled) }
        val (message, iconRes) = if (options.value.torchEnabled) {
            R.string.notification_chip_message_torch_enabled to R.drawable.ic_torch_filled
        } else {
            R.string.notification_chip_message_torch_disabled to R.drawable.ic_torch_outlined
        }
        emitNotification(NotificationEvent(message = message, iconRes = iconRes))
    }

    private fun togglePlane() {
        options.update { it.copy(planeEnabled = !it.planeEnabled) }
        val (message, iconRes) = if (options.value.planeEnabled) {
            R.string.notification_chip_message_plane_enabled to R.drawable.ic_plane_renderer_on
        } else {
            R.string.notification_chip_message_plane_disabled to R.drawable.ic_plane_renderer_off
        }
        emitNotification(NotificationEvent(message = message, iconRes = iconRes))
    }

    private fun emitNotification(event: NotificationEvent) {
        viewModelScope.launch { _notification.emit(event) }
    }
}

sealed interface ModelsEvent {
    data object OnCheckPermissionsStates : ModelsEvent
    data class OnPermissionInteraction(val manifestString: ManifestString) : ModelsEvent

    data class OnSelectMarker(val marker: Marker) : ModelsEvent

    data class OnRepositionOrigin(
        val originNode: OriginNode?,
        val markerNode: MarkerNode?
    ) : ModelsEvent

    data class OnCalibrateOriginToMarker(
        val originNode: OriginNode?,
        val markerNode: MarkerNode?
    ) : ModelsEvent

    data class OnSelectContainer(
        val containerNode: ContainerNode,
        val materialLoader: MaterialLoader
    ) : ModelsEvent

    data class OnCancelContainerCalibration(
        val containerNode: ContainerNode?,
        val materialLoader: MaterialLoader
    ) : ModelsEvent

    data class OnCalibrateContainerToOrigin(
        val containerNode: ContainerNode?,
        val materialLoader: MaterialLoader
    ) : ModelsEvent

    data class OnInsertModel(val contentUri: Uri) : ModelsEvent
    data object OnDeleteModel : ModelsEvent

    data class OnChangeMeasurement(val measurement: Measurement) : ModelsEvent
    data class OnChangeMeasurementMode(val mode: MeasurementMode) : ModelsEvent

    data class OnRestoreContainerDefaults(val container: ContainerNode?) : ModelsEvent
    data class OnTickDragged(val container: ContainerNode?, val axis: Axis, val tick: Int) :
        ModelsEvent

    data object OnToggleTorch : ModelsEvent
    data object OnTogglePlane : ModelsEvent
}

sealed interface ModelsCalibrationUiState {
    data object Loading : ModelsCalibrationUiState

    data object EmptyMarkers : ModelsCalibrationUiState

    @Immutable
    data class Success(
        val data: CalibrationData = CalibrationData(),
        val items: CalibrationItems = CalibrationItems(),
        val options: CalibrationOptions = CalibrationOptions()
    ) : ModelsCalibrationUiState
}

@Immutable
data class CalibrationData(
    val markers: List<Marker> = emptyList(),
    val models: List<Model> = emptyList()
)

@Immutable
data class CalibrationItems(
    val selectedModelId: Int? = null,
    val currentMeasurement: Measurement = Measurement.TRANSLATION,
    val currentMode: MeasurementMode = MeasurementMode(),
    val selectedMarker: Marker? = null,
    val currentBitmap: BitmapInfo? = null
)

@Immutable
data class BitmapInfo(
    val markerId: Int,
    val bitmap: Bitmap
)

@Immutable
data class CalibrationOptions(
    val torchEnabled: Boolean = false,
    val planeEnabled: Boolean = true
)