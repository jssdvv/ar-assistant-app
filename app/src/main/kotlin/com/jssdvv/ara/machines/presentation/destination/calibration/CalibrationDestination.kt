package com.jssdvv.ara.machines.presentation.destination.calibration

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Session
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.AddIcon
import com.jssdvv.ara.core.presentation.common.component.DeniedPermissions
import com.jssdvv.ara.core.presentation.common.component.PermanentlyDeniedPermissions
import com.jssdvv.ara.core.presentation.common.state.Permission
import com.jssdvv.ara.core.presentation.common.state.allGranted
import com.jssdvv.ara.core.presentation.common.state.anyDenied
import com.jssdvv.ara.core.presentation.common.state.anyPermanentlyDenied
import com.jssdvv.ara.core.presentation.common.state.deniedPermissions
import com.jssdvv.ara.core.presentation.common.state.permanentlyDeniedRationales
import com.jssdvv.ara.core.presentation.foundation.component.ARSceneSurface
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.component.NotificationChip
import com.jssdvv.ara.machines.presentation.component.RenderableIcon
import com.jssdvv.ara.machines.presentation.component.SceneMarkerIconButton
import com.jssdvv.ara.machines.presentation.component.ShutterActionButton
import com.jssdvv.ara.machines.presentation.component.ShutterSection
import com.jssdvv.ara.machines.presentation.destination.ar_session.NotificationEvent
import com.jssdvv.ara.machines.presentation.destination.calibration.component.CalibrationBottomSheetContent
import com.jssdvv.ara.machines.presentation.destination.calibration.component.ModelsDialog
import com.jssdvv.ara.machines.presentation.destination.calibration.component.OptionsRow
import com.jssdvv.ara.machines.presentation.destination.calibration.component.SelectedMarkerDialog
import com.jssdvv.ara.machines.presentation.destination.calibration.component.UnsavedChangesDialog
import com.jssdvv.ara.machines.presentation.destination.steps.component.BottomSheetMainHeader
import com.jssdvv.ara.machines.presentation.destination.steps.component.DraggableBottomSheet
import com.jssdvv.ara.machines.presentation.sceneview.node.ContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.ContainerNodesMap
import com.jssdvv.ara.machines.presentation.sceneview.utility.configureARSession
import com.jssdvv.ara.machines.presentation.sceneview.utility.detectMarkerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.findAncestorOrNull
import com.jssdvv.ara.machines.presentation.sceneview.utility.markerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberNodes
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberOriginNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.safeTerminate
import com.jssdvv.ara.machines.presentation.sceneview.utility.setImageDatabase
import com.jssdvv.ara.machines.presentation.sceneview.utility.setTorch
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CalibrationDestination(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToMarkers: (machineId: Int) -> Unit,
    viewModel: ModelsCalibrationViewModel = hiltViewModel(),
) = ModelsCalibrationScreen(
    notification = viewModel.notification,
    permissions = viewModel.permissions.collectAsStateWithLifecycle().value,
    uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
    onEvent = viewModel::onEvent,
    onNavigateBack = onNavigateBack,
    onNavigateToMarkers = { onNavigateToMarkers(viewModel.machineId) },
    modifier = modifier
)

@Composable
internal fun ModelsCalibrationScreen(
    notification: SharedFlow<NotificationEvent>,
    permissions: Set<Permission>,
    uiState: ModelsCalibrationUiState,
    onEvent: (ModelsEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMarkers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onEvent(ModelsEvent.OnCheckPermissionsStates) }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        permissionsResult.forEach {
            onEvent(ModelsEvent.OnPermissionInteraction(it.key))
            activity?.shouldShowRequestPermissionRationale(it.key)
        }
        onEvent(ModelsEvent.OnCheckPermissionsStates)
    }

    when {
        permissions.allGranted() -> {
            when (uiState) {
                ModelsCalibrationUiState.Loading -> LoadingWheelScreen()

                ModelsCalibrationUiState.EmptyMarkers -> {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            MaterialTheme.spacing.medium,
                            Alignment.CenterVertically
                        )
                    ) {
                        Text(stringResource(R.string.calibration_no_markers_title))
                        Button(
                            onClick = onNavigateToMarkers,
                            content = { Text(stringResource(R.string.calibration_button_go_to_markers_creation_action)) }
                        )
                    }
                }

                is ModelsCalibrationUiState.Success -> {
                    CalibrationContent(
                        notification = notification,
                        modifier = modifier,
                        data = uiState.data,
                        items = uiState.items,
                        options = uiState.options,
                        onEvent = onEvent,
                        onNavigateUp = onNavigateBack
                    )
                }
            }
        }

        permissions.anyPermanentlyDenied() -> PermanentlyDeniedPermissions(
            permanentlyDeniedRationales = permissions.permanentlyDeniedRationales(),
            settingsLauncher = settingsLauncher
        )

        permissions.anyDenied() -> DeniedPermissions(
            deniedPermissions = permissions.deniedPermissions(),
            permissionLauncher = permissionLauncher
        )
    }

}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun CalibrationContent(
    notification: SharedFlow<NotificationEvent>,
    data: CalibrationData,
    items: CalibrationItems,
    options: CalibrationOptions,
    onEvent: (ModelsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val view = rememberView(engine)

    var session by remember { mutableStateOf<Session?>(null) }
    var trackingMethod by remember { mutableStateOf(AugmentedImage.TrackingMethod.NOT_TRACKING) }
    val isShutterEnabled = trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING

    // The Scene Nodes
    val origin = rememberOriginNode(engine)
    val nodes = rememberNodes { add(0, origin) }
    val marker = remember(nodes.size) { nodes.markerNode }

    // Model Edition
    val containerNodesMap: ContainerNodesMap = remember { mutableMapOf() }
    val selectedContainer = remember(items.selectedModelId) { containerNodesMap[items.selectedModelId] }
    var pressedAxis by remember { mutableStateOf<Axis?>(null) }

    // Components Visibility
    var showWarningDialog by remember { mutableStateOf(false) }
    var showMarkersDialog by remember { mutableStateOf(true) }
    var showModelsDialog by remember { mutableStateOf(false) }
    var showShutterSection by remember { mutableStateOf(true) }

    val pickModel = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            activityResult.data?.data?.let { contentUri ->
                onEvent(ModelsEvent.OnInsertModel(contentUri))
            }
        }
    )

    if (marker != null) {
        data.models.forEach { model ->
            key(model.id) {
                rememberContainerNode(model, engine) {
                    setModelNode(modelLoader, materialLoader)
                }.also { container ->
                    DisposableEffect(container) {
                        containerNodesMap[container.modelId] = container
                        origin.addChildNode(container)
                        onEvent(ModelsEvent.OnRepositionOrigin(origin, marker))
                        onDispose {
                            containerNodesMap.remove(container.modelId)
                            container.safeTerminate()
                        }
                    }
                }
            }
        }
    }

    BackHandler(data.models.any { !it.calibrated }) { showWarningDialog = true }

    LaunchedEffect(items.currentBitmap, Unit) {
        val info = items.currentBitmap ?: return@LaunchedEffect
        session?.setImageDatabase(info.markerId.toString(), info.bitmap)
    }

    LaunchedEffect(options.torchEnabled) { session?.setTorch(options.torchEnabled) }

    LaunchedEffect(pressedAxis, selectedContainer) {
        selectedContainer?.apply {
            setAxisVisibility(pressedAxis, materialLoader)
            setGizmoVisibility(pressedAxis == null, materialLoader)
        }
    }

    ARSceneSurface(
        modifier = modifier,
        onNavigationUp = {
            if (data.models.any { !it.calibrated }) showWarningDialog = true
            else onNavigateUp()
        },
        trailingAction = { SceneMarkerIconButton(Modifier.size(it)) { showMarkersDialog = true } },
        optionsRow = {
            OptionsRow(
                rowHeight = it,
                isPlaneEnabled = options.planeEnabled,
                isTorchEnabled = options.torchEnabled,
                onToggleTorch = { onEvent(ModelsEvent.OnToggleTorch) },
                onTogglePlane = { onEvent(ModelsEvent.OnTogglePlane) }
            )
        },
        notificationChip = {
            var event by remember { mutableStateOf<NotificationEvent?>(null) }
            LaunchedEffect(Unit) { notification.collectLatest { event = it } }
            NotificationChip(event) { event = null }
        },
        bottomSheet = {
            DraggableBottomSheet(
                isVisible = items.selectedModelId != null,
                header = {
                    BottomSheetMainHeader(
                        title = "Edit Model",
                        onSaveClick = {
                            onEvent(
                                ModelsEvent.OnCalibrateContainerToOrigin(
                                    containerNode = selectedContainer,
                                    materialLoader = materialLoader
                                )
                            )
                            showShutterSection = true
                        },
                        onCancelClick = {
                            onEvent(
                                ModelsEvent.OnCancelContainerCalibration(
                                    containerNode = selectedContainer,
                                    materialLoader = materialLoader
                                )
                            )
                            showShutterSection = true
                        }
                    )
                }
            ) {
                CalibrationBottomSheetContent(
                    onRestoreDefaults = {
                        onEvent(ModelsEvent.OnRestoreContainerDefaults(selectedContainer))
                    },
                    measurement = items.currentMeasurement,
                    mode = items.currentMode,
                    onMeasurementChange = { onEvent(ModelsEvent.OnChangeMeasurement(it)) },
                    onModeChange = { onEvent(ModelsEvent.OnChangeMeasurementMode(it)) },
                    onDeleteModel = { onEvent(ModelsEvent.OnDeleteModel) },
                    onAxisPressed = { axis, pressed -> pressedAxis = if (pressed) axis else null },
                    onTickDragged = { axis, tick ->
                        onEvent(ModelsEvent.OnTickDragged(selectedContainer, axis, tick))
                    }
                )
            }
        }
    ) {
        ARScene(
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            childNodes = nodes,
            planeRenderer = options.planeEnabled, // Dots on detected flat surfaces
            sessionConfiguration = ::configureARSession,
            onSessionCreated = { session = it },
            onSessionUpdated = { _, frame ->
                // Gets the last detected trackable (QR code) in any
                // previous frame as AugmentedImages in the scene
                frame.getUpdatedAugmentedImages().forEach { trackable ->
                    if (marker?.name == trackable.name) return@forEach

                    if (marker != null) {
                        marker.updateTrackable(trackable)
                        return@forEach
                    }

                    if (items.selectedMarker != null) {
                        trackable.detectMarkerNode(
                            engine = engine,
                            materialLoader = materialLoader,
                            onTrackingMethodChanged = { trackingMethod = it },
                            onMarkerDetected = { detectedMarker ->
                                nodes.add(1, detectedMarker)
                                onEvent(ModelsEvent.OnRepositionOrigin(origin, marker))
                            }
                        )
                    }
                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { _, node ->
                    node?.findAncestorOrNull<ContainerNode>()?.let { container ->
                        onEvent(ModelsEvent.OnSelectContainer(container, materialLoader))
                        showShutterSection = false
                    }
                }
            )
        )

        ShutterSection(
            visible = showShutterSection,
            shutterClickEnabled = isShutterEnabled,
            shutterPressEnabled = isShutterEnabled,
            onClickShutter = { onEvent(ModelsEvent.OnCalibrateOriginToMarker(origin, marker)) },
            onPressShutter = { onEvent(ModelsEvent.OnRepositionOrigin(origin, marker)) },
            modifier = Modifier.align(Alignment.BottomCenter),
            leftSection = {
                ShutterActionButton(
                    onClick = {
                        showModelsDialog = true
                        showShutterSection = false
                    },
                    modifier = Modifier.padding(MaterialTheme.spacing.small),
                    enabled = data.models.isNotEmpty() && marker != null,
                    iconInFront = true,
                    icon = { RenderableIcon() },
                    content = { Text(stringResource(R.string.calibration_shutter_button_models_label)) }
                )
            },
            rightSection = {
                ShutterActionButton(
                    onClick = {
                        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*"
                            pickModel.launch(this)
                        }
                    },
                    modifier = Modifier.padding(MaterialTheme.spacing.small),
                    enabled = marker != null,
                    icon = { AddIcon() },
                    content = { Text(stringResource(R.string.button_add_action)) }
                )
            },
            iconDrawableId = R.drawable.ic_calibrate_to
        )

        if (showMarkersDialog) {
            SelectedMarkerDialog(
                onDismissRequest = { showMarkersDialog = false },
                markers = data.markers,
                currentMarker = items.selectedMarker,
                onMarkerClick = { marker ->
                    onEvent(ModelsEvent.OnSelectMarker(marker))
                    showMarkersDialog = false
                }
            )
        }

        if (showWarningDialog) {
            UnsavedChangesDialog(
                onDismissRequest = { showWarningDialog = false },
                onConfirm = {
                    showWarningDialog = false
                    //todo add deletion of model if not calibrated
                    onNavigateUp()
                },
                onOpenModels = {
                    showWarningDialog = false
                    showModelsDialog = true
                }
            )
        }

        if (showModelsDialog) {
            ModelsDialog(
                models = data.models,
                onSelectModel = { modelId ->
                    val container = containerNodesMap[modelId] ?: return@ModelsDialog
                    onEvent(ModelsEvent.OnSelectContainer(container, materialLoader))
                    showModelsDialog = false
                },
                onDismissRequest = {
                    showModelsDialog = false
                    showShutterSection = true
                }
            )
        }
    }
}