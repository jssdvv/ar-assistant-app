package com.jssdvv.ara.machines.presentation.destination.calibration

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Session
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.PermissionState
import com.jssdvv.ara.core.presentation.common.AddIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheel
import com.jssdvv.ara.core.presentation.foundation.component.SceneSurface
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.utility.ContainerNode
import com.jssdvv.ara.machines.domain.utility.OriginNode
import com.jssdvv.ara.machines.domain.utility.applyObjectPositionOffset
import com.jssdvv.ara.machines.domain.utility.applyObjectQuaternionOffset
import com.jssdvv.ara.machines.domain.utility.configureARSession
import com.jssdvv.ara.machines.domain.utility.createContainerNode
import com.jssdvv.ara.machines.domain.utility.detectMarkerNode
import com.jssdvv.ara.machines.domain.utility.filterMarkerNodes
import com.jssdvv.ara.machines.domain.utility.filterModelNodes
import com.jssdvv.ara.machines.domain.utility.findModelInContainerFromRenderable
import com.jssdvv.ara.machines.domain.utility.generateGizmoNode
import com.jssdvv.ara.machines.domain.utility.isolateAxisVisibility
import com.jssdvv.ara.machines.domain.utility.offset
import com.jssdvv.ara.machines.domain.utility.safeTerminate
import com.jssdvv.ara.machines.domain.utility.setGizmoVisibility
import com.jssdvv.ara.machines.domain.utility.setImageDatabase
import com.jssdvv.ara.machines.domain.utility.setSelectedMaterialInstance
import com.jssdvv.ara.machines.domain.utility.setTorch
import com.jssdvv.ara.machines.domain.utility.setUnselectedMaterialInstance
import com.jssdvv.ara.machines.domain.utility.unidirectionalRotation
import com.jssdvv.ara.machines.domain.utility.unidirectionalTranslation
import com.jssdvv.ara.machines.presentation.component.NotificationChip
import com.jssdvv.ara.machines.presentation.component.SceneMarkerIconButton
import com.jssdvv.ara.machines.presentation.component.ShutterSection
import com.jssdvv.ara.machines.presentation.destination.ar_session.NotificationEvent
import com.jssdvv.ara.machines.presentation.destination.calibration.component.CalibrationBottomSheetContent
import com.jssdvv.ara.machines.presentation.destination.calibration.component.OptionsRow
import com.jssdvv.ara.machines.presentation.destination.calibration.component.SelectedMarkerDialog
import com.jssdvv.ara.machines.presentation.destination.calibration.component.UnsavedChangesDialog
import com.jssdvv.ara.machines.presentation.destination.calibration.function.Transformation
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TransformationMode
import com.jssdvv.ara.machines.presentation.destination.steps.component.BottomSheetMainHeader
import com.jssdvv.ara.machines.presentation.destination.steps.component.DraggableBottomSheet
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CalibrationDestination(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToMarkers: (machineId: Int) -> Unit,
    viewModel: ModelsCalibrationViewModel = hiltViewModel(),
) {
    val permissionsStates by viewModel.permissionsStates.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ModelsCalibrationScreen(
        notification = viewModel.notification,
        modifier = modifier,
        permissionsStates = permissionsStates,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToMarkers = { onNavigateToMarkers(viewModel.machineId) }
    )
}

@Composable
internal fun ModelsCalibrationScreen(
    notification: SharedFlow<NotificationEvent>,
    permissionsStates: List<Pair<String, PermissionState>>,
    uiState: ModelsCalibrationUiState,
    onEvent: (ModelsEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMarkers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val permissions = ModelsCalibrationViewModel.permissions

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onEvent(ModelsEvent.OnCheckPermissionsStates(permissions.map { it to true })) }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val permissionsPair = permissionsResult.map {
            onEvent(ModelsEvent.OnPermissionInteraction(it.key))
            it.key to (context as Activity).shouldShowRequestPermissionRationale(it.key)
        }
        onEvent(ModelsEvent.OnCheckPermissionsStates(permissionsPair))
    }

    when {
        permissionsStates.all { it.second is PermissionState.Granted } -> {
            when (uiState) {
                ModelsCalibrationUiState.Loading -> {
                    LoadingWheel(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is ModelsCalibrationUiState.Success -> {
                    SuccessModelsCalibrationScreen(
                        notification = notification,
                        modifier = modifier,
                        markers = uiState.markers,
                        models = uiState.models,
                        selectedMarker = uiState.selectedMarker,
                        currentBitmapInfo = uiState.selectedMarkerBitmap,
                        options = uiState.options,
                        onEvent = onEvent,
                        onNavigateUp = onNavigateBack,
                        onNavigateToMarkers = onNavigateToMarkers
                    )
                }
            }
        }

        permissionsStates.any { it.second is PermissionState.PermanentlyDenied } -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val permanentlyDeniedPermissions =
                    permissionsStates.filter { it.second is PermissionState.PermanentlyDenied }

                permanentlyDeniedPermissions.forEach {
                    Text(stringResource((it.second as PermissionState.PermanentlyDenied).rationaleId))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        settingsLauncher.launch(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    },
                    content = { Text(stringResource(R.string.button_permissions_grant_action)) }
                )
            }
        }

        permissionsStates.any { it.second is PermissionState.Denied } -> {
            val deniedPermissions = permissionsStates.filter { it.second is PermissionState.Denied }

            val permissionsToRequest = deniedPermissions.map { it.first }.toTypedArray()

            LaunchedEffect(true) {
                permissionLauncher.launch(permissionsToRequest)
            }
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                deniedPermissions.forEach {
                    Text(stringResource((it.second as PermissionState.Denied).rationaleId))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { permissionLauncher.launch(permissionsToRequest) }) {
                    Text(stringResource(R.string.button_permissions_request_again_action))
                }
            }
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun SuccessModelsCalibrationScreen(
    notification: SharedFlow<NotificationEvent>,
    markers: List<Marker>,
    models: List<Model>,
    selectedMarker: Marker?,
    currentBitmapInfo: BitmapInfo?,
    options: CalibrationOptions,
    onEvent: (ModelsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToMarkers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val view = rememberView(engine).apply { isStencilBufferEnabled = true }
    var session by remember { mutableStateOf<Session?>(null) }

    // The Scene Nodes
    val originNode = remember { OriginNode(engine) }
    val nodes = rememberNodes { add(0, originNode) }
    val markerNode = remember(nodes.size) { nodes.filterMarkerNodes().firstOrNull() }

    // Model Edition
    var selectedContainer by remember { mutableStateOf<ContainerNode?>(null) }
    val isEditionEnabled by remember { derivedStateOf { selectedContainer != null } }
    var previousModelTransform: Transform by remember { mutableStateOf(Transform()) }

    // Editor Toggle Options
    var transformation by remember { mutableStateOf(Transformation.TRANSLATION) }
    var mode by remember { mutableStateOf(TransformationMode()) }

    var firstTimeDetected by remember { mutableStateOf(false) }

    var trackingMethod by remember { mutableStateOf(AugmentedImage.TrackingMethod.NOT_TRACKING) }
    val isShutterEnabled = trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
    var pressedAxis by remember { mutableStateOf<Axis?>(null) }

    // Dialogs
    var showWarningDialog by remember { mutableStateOf(false) }
    var showMarkersDialog by remember { mutableStateOf(true) }

    val pickModel = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            activityResult.data?.data?.let { contentUri ->
                onEvent(ModelsEvent.OnInsertModel(contentUri))
            }
        }
    )

    val repositionOrigin = remember(selectedMarker, markerNode) {
        {
            if (selectedMarker != null && markerNode != null) {
                originNode.offset(
                    markerNode = markerNode,
                    offsetPosition = selectedMarker.originOffsetPosition,
                    offsetQuaternion = selectedMarker.originOffsetRotation
                )
            }
        }
    }

    val calibrateOriginToMarker = remember(selectedMarker, markerNode) {
        {
            // Update the position and rotation of the Origin node in the Marker's local space
            if (selectedMarker != null && markerNode != null) {
                onEvent(
                    ModelsEvent.OnCalibrateOriginToMarker(
                        markerId = selectedMarker.id,
                        newOriginPosition = markerNode.getLocalPosition(originNode.worldPosition),
                        newOriginQuaternion = markerNode.getLocalQuaternion(originNode.worldQuaternion)
                    )
                )
            }
        }
    }

    val calibrateModel = remember(selectedContainer) {
        {
            selectedContainer?.apply {
                onEvent(ModelsEvent.OnCalibrateModelsToOrigin(modelId, position, quaternion))
                selectedContainer = null
            }
        }
    }

    if (firstTimeDetected) {
        models.forEach { model ->
            key(model.id) {
                val containerNode = remember(model.id) {
                    createContainerNode(
                        engine = engine,
                        modelLoader = modelLoader,
                        materialLoader = materialLoader,
                        model = model
                    ).apply {
                        position = model.offsetPosition
                        quaternion = model.offsetRotation
                        generateGizmoNode(materialLoader)
                    }
                }

                DisposableEffect(containerNode) {
                    originNode.addChildNode(containerNode)
                    repositionOrigin()
                    onDispose { nodes.safeTerminate(containerNode) }
                }
            }
        }
    }

    BackHandler(markers.any { !it.calibrated }) { showWarningDialog = true }

    LaunchedEffect(currentBitmapInfo, Unit) {
        val info = currentBitmapInfo ?: return@LaunchedEffect
        session?.setImageDatabase(info.markerId.toString(), info.bitmap) ?: return@LaunchedEffect
        nodes.safeTerminate(nodes.filterMarkerNodes())
    }

    LaunchedEffect(options.isTorchEnabled) {
        session?.setTorch(options.isTorchEnabled)
    }

    LaunchedEffect(pressedAxis) {
        pressedAxis?.let {
            selectedContainer?.isolateAxisVisibility(it, materialLoader)
            selectedContainer?.setGizmoVisibility(false)
            return@LaunchedEffect
        }
        delay(100)
        selectedContainer?.isolateAxisVisibility(null, materialLoader)
        if(isEditionEnabled) {
            selectedContainer?.setGizmoVisibility(true)
        } else {
            selectedContainer?.setGizmoVisibility(false)
        }
    }

    SceneSurface(
        modifier = modifier,
        onNavigationUp = onNavigateUp,
        trailingAction = { rowHeight ->
            SceneMarkerIconButton(
                onClick = { showMarkersDialog = true },
                modifier = Modifier.size(rowHeight)
            )
        },
        optionsRow = {
            OptionsRow(
                rowHeight = it,
                isPlaneEnabled = options.isPlaneEnabled,
                isTorchEnabled = options.isTorchEnabled,
                onToggleTorch = { onEvent(ModelsEvent.OnToggleTorch) },
                onTogglePlane = { onEvent(ModelsEvent.OnTogglePlane) }
            )
        },
        notificationChip = {
            var notificationMessage by remember { mutableStateOf<NotificationEvent?>(null) }
            LaunchedEffect(Unit) { notification.collectLatest { notificationMessage = it } }
            NotificationChip(
                event = notificationMessage,
                onDismiss = { notificationMessage = null }
            )
        },
        bottomSheet = {
            DraggableBottomSheet(
                isVisible = isEditionEnabled,
                header = {
                    BottomSheetMainHeader(
                        title = "Edit Model",
                        onSaveClick = {
                            selectedContainer?.apply {
                                childNodes.filterModelNodes()
                                    .forEach { it.setUnselectedMaterialInstance(materialLoader) }
                                setGizmoVisibility(false)
                            }
                            calibrateModel()
                        },
                        onCancelClick = {
                            selectedContainer?.apply {
                                childNodes.filterModelNodes()
                                    .forEach { it.setUnselectedMaterialInstance(materialLoader) }
                                setGizmoVisibility(false)
                                transform = previousModelTransform
                            }
                            selectedContainer = null
                        }
                    )
                }
            ) {
                CalibrationBottomSheetContent(
                    onRestoreDefaults = {
                        when (transformation) {
                            Transformation.TRANSLATION -> {
                                selectedContainer?.worldPosition = Position()
                            }

                            Transformation.ROTATION -> {
                                selectedContainer?.worldQuaternion = Quaternion()
                            }
                        }
                    },
                    transformation = transformation,
                    mode = mode,
                    onTransformationChange = { transformation = it },
                    onModeChange = { mode = it },
                    onDeleteModel = {}, // todo add this
                    onAxisPressed = { axis, isPressed ->
                        pressedAxis = if (isPressed) axis else null
                    },
                    onTickDragged = { axis, tick ->
                        when (transformation) {
                            Transformation.TRANSLATION -> {
                                val millis = mode.translation.mmPerUnit * tick / 1000F
                                val offset = unidirectionalTranslation(axis, millis)
                                selectedContainer?.applyObjectPositionOffset(offset)
                            }

                            Transformation.ROTATION -> {
                                val degrees = mode.rotation.halfDegPerUnit * tick / 2F
                                val offset = unidirectionalRotation(axis, degrees)
                                selectedContainer?.applyObjectQuaternionOffset(offset)
                            }
                        }
                    }
                )
            }
        }
    ) {
        if (markers.isNotEmpty()) {
            ARScene(
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                childNodes = nodes,
                planeRenderer = options.isPlaneEnabled, // Dots on detected flat surfaces
                sessionConfiguration = ::configureARSession,
                onSessionCreated = { session = it },
                onSessionUpdated = { _, frame ->
                    // Gets the last detected trackable (QR code) in any
                    // previous frame as AugmentedImages in the scene
                    frame.getUpdatedAugmentedImages().forEach { trackable ->
                        val markerNodes = nodes.filterMarkerNodes()
                        if (markerNodes.none { it.imageName == trackable.name }) {
                            nodes.safeTerminate(markerNodes)
                            trackable.detectMarkerNode(
                                engine = engine,
                                materialLoader = materialLoader,
                                onTrackingMethodChanged = { trackingMethod = it },
                                onMarkerDetected = {
                                    nodes.add(it)
                                    if (selectedMarker != null && !firstTimeDetected) {
                                        firstTimeDetected = true
                                        repositionOrigin()
                                    }
                                }
                            )
                        }
                    }
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { _, node ->
                        if (selectedContainer == null) {
                            node?.findModelInContainerFromRenderable { containerNode, modelNode ->
                                selectedContainer = containerNode
                                previousModelTransform = containerNode.transform
                                modelNode.setSelectedMaterialInstance(materialLoader)
                                containerNode.setGizmoVisibility(true)
                            }
                        }
                    }
                )
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = !isEditionEnabled,
            enter = expandVertically(tween(), Alignment.Top),
            exit = shrinkVertically(tween(), Alignment.Top)
        ) {
            ShutterSection(
                shutterEnabled = isShutterEnabled,
                onClickShutter = {
                    calibrateOriginToMarker()
                    val machineId = selectedMarker?.machineId ?: return@ShutterSection
                    val markerIndex = selectedMarker.index
                    val message = context.getString(
                        R.string.toast_marker_calibration_success,
                        machineId.toString(),
                        markerIndex
                    )
                    val infoToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                    infoToast.show()
                },
                onPressShutter = {
                    repositionOrigin()
                    val infoToast = Toast.makeText(
                        context,
                        "Model repositioned to M${selectedMarker?.machineId}P${selectedMarker?.index}",
                        Toast.LENGTH_SHORT
                    )
                    infoToast.show()
                },
                rightSection = {
                    ButtonWithIcon(
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "*/*"
                            }
                            pickModel.launch(intent)
                        },
                        modifier = Modifier
                            .padding(end = MaterialTheme.spacing.small)
                            .align(Alignment.CenterEnd),
                        enabled = firstTimeDetected,
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        iconInFront = false,
                        icon = { AddIcon() },
                        content = { Text("Agregar") } // todo create string
                    )
                },
                iconDrawableId = R.drawable.ic_calibrate_to
            )
        }
    }

    if (showMarkersDialog) {
        SelectedMarkerDialog(
            modifier = Modifier,
            onDismissRequest = { showMarkersDialog = false },
            markers = markers,
            currentMarker = selectedMarker,
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
                //todo add deletion of model if not calibrated
                showWarningDialog = false
                onNavigateUp()
            },
            onOpenMarkers = {
                showWarningDialog = false
                showMarkersDialog = true
            }
        )
    }
}