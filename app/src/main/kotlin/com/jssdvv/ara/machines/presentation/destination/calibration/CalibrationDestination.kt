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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.core.net.toFile
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Session
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.PermissionState
import com.jssdvv.ara.core.domain.utility.forEachApply
import com.jssdvv.ara.core.presentation.common.AddIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheel
import com.jssdvv.ara.core.presentation.foundation.component.SceneSurface
import com.jssdvv.ara.core.presentation.navigation.MarkerIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.core.presentation.theme.tubShapes
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.utility.configureARSession
import com.jssdvv.ara.machines.domain.utility.setSingleImageDatabase
import com.jssdvv.ara.machines.domain.utility.unidirectionalRotation
import com.jssdvv.ara.machines.presentation.component.ShutterSection
import com.jssdvv.ara.machines.presentation.destination.calibration.component.EditorOptions
import com.jssdvv.ara.machines.presentation.destination.calibration.component.SelectedMarkerDialog
import com.jssdvv.ara.machines.presentation.destination.calibration.component.UnsavedChangesDialog
import com.jssdvv.ara.machines.presentation.destination.calibration.function.MODEL_SELECTED_COLOR
import com.jssdvv.ara.machines.presentation.destination.calibration.function.MODEL_UNSELECTED_COLOR
import com.jssdvv.ara.machines.presentation.destination.calibration.function.Transformation
import com.jssdvv.ara.machines.presentation.destination.calibration.function.TransformationMode
import com.jssdvv.ara.machines.presentation.destination.calibration.function.createModelNode
import com.jssdvv.ara.machines.presentation.destination.calibration.function.detectMarker
import com.jssdvv.ara.machines.presentation.destination.calibration.function.getModelMaterialInstance
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

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
                        modifier = modifier,
                        markers = uiState.markers,
                        models = uiState.models,
                        selectedMarker = uiState.selectedMarker,
                        currentBitmapInfo = uiState.selectedMarkerBitmap,
                        onNavigateUp = onNavigateBack,
                        onNavigateToMarkers = onNavigateToMarkers,
                        onEvent = onEvent
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
    markers: List<Marker>,
    models: List<Model>,
    selectedMarker: Marker?,
    currentBitmapInfo: BitmapInfo?,
    onEvent: (ModelsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToMarkers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // AR Scene
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val view = rememberView(engine).apply { isStencilBufferEnabled = true }
    var arSession by remember { mutableStateOf<Session?>(null) }

    val originNode = remember { Node(engine).apply { worldTransform = Transform() } }
    val nodes = rememberNodes { add(0, originNode) }

    // The Scene Nodes
    val markerNode = remember(nodes.size) {
        nodes.filterIsInstance<AugmentedImageNode>().firstOrNull()
    }

    val modelNodes = remember(originNode.childNodes.size) {
        nodes.filterIsInstance<ModelNode>()
    }

    // Model Edition
    var selectedModel by remember { mutableStateOf<ModelNode?>(null) }
    val isEditionEnabled by remember { derivedStateOf { selectedModel != null } }
    var previousModelTransform: Transform by remember { mutableStateOf(Transform()) }

    // Editor Toggle Options
    var transformation by remember { mutableStateOf(Transformation.TRANSLATION) }
    var transformMode by remember { mutableStateOf(TransformationMode()) }

    var firstTimeDetected by remember { mutableStateOf(false) }

    var trackingMethod by remember { mutableStateOf(AugmentedImage.TrackingMethod.NOT_TRACKING) }
    val isShutterEnabled = trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING

    // Dialogs
    var showWarningDialog by remember { mutableStateOf(false) }
    var showMarkersDialog by remember { mutableStateOf(true) }

    // Model Materials
    val selectedMaterialInstance = getModelMaterialInstance(materialLoader, MODEL_SELECTED_COLOR)
    val unselectedMaterialInstance =
        getModelMaterialInstance(materialLoader, MODEL_UNSELECTED_COLOR)

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
                originNode.position = markerNode.getWorldPosition(selectedMarker.originOffsetPosition)
                originNode.quaternion = markerNode.getWorldQuaternion(selectedMarker.originOffsetRotation)
            }
        }
    }

    val calibrateOriginToMarker = remember(selectedMarker, markerNode) {
        {
            if (selectedMarker != null && markerNode != null) {
                // Update the position and rotation of the Origin node in the Marker's local space
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

    val calibrateModel = remember(selectedModel) {
        {
            if (selectedModel != null) {
                onEvent(
                    ModelsEvent.OnCalibrateModelsToOrigin(
                        modelId = selectedModel!!.name!!.toInt(),
                        newModelPosition = selectedModel!!.position,
                        newModelQuaternion = selectedModel!!.quaternion
                    )
                )
                selectedModel = null
            }
        }
    }

    val applyTranslation = { delta: Position ->
        selectedModel?.apply { worldPosition += worldQuaternion * delta }
    }

    val applyRotation = { euler: Float, axis: Axis ->
        selectedModel?.apply { worldQuaternion *= unidirectionalRotation(axis, euler) }
    }

    if (firstTimeDetected) {
        models.forEach { model ->
            key(model.id) {
                DisposableEffect(model.id) {
                    val node = createModelNode(
                        engine = engine,
                        modelLoader = modelLoader,
                        materialLoader = materialLoader,
                        modelFile = model.glbUri.toFile(),
                        modelId = model.id
                    ).apply {
                        parent = originNode
                        position = model.offsetPosition
                        quaternion = model.offsetRotation
                    }
                    repositionOrigin()
                    onDispose { node.destroy() }
                }
            }
        }
    }

    BackHandler(markers.any { !it.calibrated }) { showWarningDialog = true }

    LaunchedEffect(currentBitmapInfo, Unit) {
        val bitmapInfo = currentBitmapInfo ?: return@LaunchedEffect
        val session = arSession ?: return@LaunchedEffect
        nodes.filterIsInstance<AugmentedImageNode>().forEach { it.destroy() }
        setSingleImageDatabase(session, bitmapInfo.markerId.toString(), bitmapInfo.bitmap)
    }

    SceneSurface(
        onNavigationUp = onNavigateUp,
        trailingAction = { rowHeight ->
            IconButton(
                onClick = { showMarkersDialog = true },
                modifier = Modifier.size(rowHeight),
                colors = IconButtonDefaults.filledIconButtonColors().copy(
                    containerColor = Color.Black.copy(alpha = 0.3F),
                    contentColor = Color.White
                ),
                content = { MarkerIcon() }
            )

            // todo fix where to locate this button
//            if (isEditionEnabled) {
//                IconButton(
//                    modifier = Modifier.size(rowHeight),
//                    onClick = { },
//                    colors = IconButtonDefaults.filledIconButtonColors().copy(
//                        containerColor = Color.Black.copy(alpha = 0.5F),
//                        contentColor = Color.White
//                    ),
//                    content = { DeleteIcon() }
//                )
//            }
        },
    ) {
        if (markers.isNotEmpty()) {
            ARScene(
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                childNodes = nodes,
                planeRenderer = false, // Turns off the dots on detected flat surfaces
                sessionConfiguration = ::configureARSession,
                onSessionCreated = { arSession = it },
                onSessionUpdated = { _, frame ->
                    // Gets the last detected trackable (QR code) in any
                    // previous frame as AugmentedImages in the scene
                    val trackables = frame.getUpdatedTrackables(AugmentedImage::class.java)

                    trackables.forEach { trackable ->

                        val markerNodes = nodes.filterIsInstance<AugmentedImageNode>()

                        if (markerNodes.none { it.imageName == trackable.name }) {

                            markerNodes.forEach {
                                it.clearChildNodes()
                                nodes.remove(it)
                            }

                            detectMarker(
                                engine = engine,
                                trackable = trackable,
                                materialLoader = materialLoader,
                                onTrackingMethodChanged = { trackingMethod = it },
                            ) { newMarkerNode ->
                                nodes.add(newMarkerNode)

                                if (selectedMarker != null && !firstTimeDetected) {
                                    firstTimeDetected = true
                                    repositionOrigin()
                                }
                            }
                        }
                    }
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { _, node ->
                        if (node is ModelNode && selectedModel == null) {
                            selectedModel = node.apply {
                                setMaterialInstance(selectedMaterialInstance)
                                childNodes.forEachApply { isVisible = true }
                                previousModelTransform = transform
                            }
                        }
                    }
                )
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // Shutter Options
            AnimatedVisibility(
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
                        ButtonWithIcon (
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

            // Editor Options
            AnimatedVisibility(
                visible = isEditionEnabled,
                enter = expandVertically(tween(), Alignment.Top),
                exit = shrinkVertically(tween(), Alignment.Top)
            ) {
                EditorOptions(
                    modifier = Modifier.background(Color.Black, MaterialTheme.tubShapes.extraLarge),
                    onRestoreDefaults = {
                        if (transformation == Transformation.TRANSLATION) {
                            selectedModel?.apply { worldPosition = Position() }
                        } else {
                            selectedModel?.apply { worldQuaternion = Quaternion() }
                        }
                    },
                    transformation = transformation,
                    transformationMode = transformMode,
                    onSaveClick = {
                        selectedModel?.apply {
                            setMaterialInstance(unselectedMaterialInstance)
                            childNodes.find { it.name == "gizmo" }?.isVisible = false
                        }
                        calibrateModel()
                    },
                    onCancelClick = {
                        selectedModel?.apply {
                            setMaterialInstance(unselectedMaterialInstance)
                            childNodes.find { it.name == "gizmo" }?.isVisible = false
                            transform = previousModelTransform
                        }
                        selectedModel = null
                    },
                    onTransformationChange = { transformation = it },
                    onTransformationModeChange = { transformMode = it },
                    onXTickDragged = { xTick ->
                        if (transformation == Transformation.TRANSLATION) {
                            applyTranslation(Position(x = transformMode.translation.mmPerUnit * xTick / 1000F))
                        } else {
                            applyRotation(
                                transformMode.rotation.halfDegPerUnit * xTick / 2F,
                                Axis.X
                            )
                        }
                    },
                    onYTickDragged = { yTick ->
                        if (transformation == Transformation.TRANSLATION) {
                            applyTranslation(Position(y = transformMode.translation.mmPerUnit * yTick / 1000F))
                        } else {
                            applyRotation(
                                transformMode.rotation.halfDegPerUnit * yTick / 2F,
                                Axis.Y
                            )
                        }
                    },
                    onZTickDragged = { zTick ->
                        if (transformation == Transformation.TRANSLATION) {
                            applyTranslation(Position(z = transformMode.translation.mmPerUnit * zTick / 1000F))
                        } else {
                            applyRotation(
                                transformMode.rotation.halfDegPerUnit * zTick / 2F,
                                Axis.Z
                            )
                        }
                    }
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
}