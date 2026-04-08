package com.jssdvv.ara.machines.presentation.destination.ar_session

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Session
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.PermissionState
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheel
import com.jssdvv.ara.core.presentation.foundation.component.SceneSurface
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.utility.ContainerNode
import com.jssdvv.ara.machines.domain.utility.OriginNode
import com.jssdvv.ara.machines.domain.utility.RenderableInfo
import com.jssdvv.ara.machines.domain.utility.applyOperationsOffsetsBeforeTo
import com.jssdvv.ara.machines.domain.utility.configureARSession
import com.jssdvv.ara.machines.domain.utility.safeTerminate
import com.jssdvv.ara.machines.domain.utility.setImageDatabase
import com.jssdvv.ara.machines.domain.utility.setTorch
import com.jssdvv.ara.machines.presentation.component.NotificationChip
import com.jssdvv.ara.machines.presentation.component.SceneMarkerIconButton
import com.jssdvv.ara.machines.presentation.component.ShutterSection
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.OptionsRow
import com.jssdvv.ara.machines.presentation.destination.calibration.BitmapInfo
import com.jssdvv.ara.machines.presentation.destination.calibration.component.SelectedMarkerDialog
import com.jssdvv.ara.machines.domain.utility.createContainerNode
import com.jssdvv.ara.machines.domain.utility.detectMarkerNode
import com.jssdvv.ara.machines.domain.utility.filterContainerNodes
import com.jssdvv.ara.machines.domain.utility.filterMarkerNodes
import com.jssdvv.ara.machines.domain.utility.filterModelNodes
import com.jssdvv.ara.machines.domain.utility.generatePivotNodes
import com.jssdvv.ara.machines.domain.utility.launchOperationAnimation
import com.jssdvv.ara.machines.domain.utility.offset
import com.jssdvv.ara.machines.domain.utility.pivotNodes
import com.jssdvv.ara.machines.domain.utility.renderableNode
import com.jssdvv.ara.machines.domain.utility.setPlayingMaterialInstance
import com.jssdvv.ara.machines.domain.utility.setUnselectedMaterialInstance
import com.jssdvv.ara.machines.presentation.destination.steps.component.AnimationIcon
import com.jssdvv.ara.machines.presentation.destination.steps.component.StepIcon
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.collections.getValue

@SuppressLint("UnrememberedMutableState")
@Composable
fun ARSessionDestination(
    onNavigateBack: () -> Unit,
    viewModel: ARSessionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionsStates by viewModel.permissionsStates.collectAsStateWithLifecycle()

    ARCameraScreen(
        permissionsStates = permissionsStates,
        notification = viewModel.notification,
        uiState = uiState,
        renderableInfoStates = viewModel.renderableInfoStates,
        onEvent = viewModel::onEvent,
        onExternalEvent = viewModel::onExternalEvent,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
internal fun ARCameraScreen(
    permissionsStates: List<Pair<String, PermissionState>>,
    notification: SharedFlow<NotificationEvent>,
    uiState: ARSessionUiState,
    renderableInfoStates: SnapshotStateMap<RenderableInfo, RenderableAnimationState>,
    onEvent: (ARSessionEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onExternalEvent: (ARSessionExternalEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val permissions = ARSessionViewModel.permissions

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onEvent(ARSessionEvent.OnCheckPermissionsStates(permissions.map { it to true })) }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val permissionsPair = permissionsResult.map {
            onEvent(ARSessionEvent.OnPermissionInteraction(it.key))
            it.key to (context as Activity).shouldShowRequestPermissionRationale(it.key)
        }
        onEvent(ARSessionEvent.OnCheckPermissionsStates(permissionsPair))
    }

    when {
        permissionsStates.all { it.second is PermissionState.Granted } -> {
            when (uiState) {
                ARSessionUiState.Loading -> {
                    LoadingWheel()
                }

                is ARSessionUiState.Success -> {
                    ARCameraContent(
                        notification = notification,
                        editorData = uiState.editorData,
                        selectedMarker = uiState.selectedMarker,
                        currentBitmapInfo = uiState.selectedMarkerBitmap,
                        selectedStep = uiState.selectedStep,
                        selectedOpTargets = uiState.selectedOperationTargets,
                        options = uiState.options,
                        renderableInfoStates = renderableInfoStates,
                        onEvent = onEvent,
                        onExternalEvent = onExternalEvent,
                        onNavigateBack = onNavigateBack
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

@Composable
fun ARCameraContent(
    notification: SharedFlow<NotificationEvent>,
    editorData: EditorData,
    selectedMarker: Marker?,
    currentBitmapInfo: BitmapInfo?,
    selectedStep: Step?,
    selectedOpTargets: OperationTargets?,
    options: ARSessionOptions,
    renderableInfoStates: SnapshotStateMap<RenderableInfo, RenderableAnimationState>,
    onEvent: (ARSessionEvent) -> Unit,
    onExternalEvent: (ARSessionExternalEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val view = rememberView(engine).apply { isStencilBufferEnabled = true }
    var session by remember { mutableStateOf<Session?>(null) }

    // Nodes
    val originNode = remember { OriginNode(engine) }
    val nodes = rememberNodes { add(0, originNode) }
    val cameraNode = rememberARCameraNode(engine)
    val containersMap = remember { mutableStateMapOf<Int, ContainerNode>() }
    val markerNode = remember(nodes.size) { nodes.filterMarkerNodes().firstOrNull() }

    var repositionChange by remember { mutableStateOf(false) } // to notify a reposition only
    var firstTimeDetected by remember { mutableStateOf(false) }
    var showMarkersDialog by remember { mutableStateOf(true) }
    var trackingMethod by remember { mutableStateOf(AugmentedImage.TrackingMethod.NOT_TRACKING) }
    val isShutterEnabled by remember {
        derivedStateOf { trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING }
    }

    val repositionOrigin = remember(selectedMarker, markerNode) {
        {
            if (selectedMarker != null && markerNode != null) {
                originNode.offset(
                    markerNode = markerNode,
                    offsetPosition = selectedMarker.originOffsetPosition,
                    offsetQuaternion = selectedMarker.originOffsetRotation
                )
                repositionChange = !repositionChange
            }
        }
    }

    if (firstTimeDetected) {
        editorData.models.forEach { model ->
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
                        childNodes.filterModelNodes().forEach { modelNode ->
                            modelNode.generatePivotNodes(materialLoader)
                        }
                    }
                }

                DisposableEffect(containerNode) {
                    originNode.addChildNode(containerNode)
                    containersMap[containerNode.modelId] = containerNode
                    repositionOrigin()
                    onDispose {
                        containersMap.remove(containerNode.modelId)
                        nodes.safeTerminate(containerNode)
                    }
                }

                LaunchedEffect(containerNode) {
                    val infoStates = mutableMapOf<RenderableInfo, RenderableAnimationState>()
                    containerNode.pivotNodes.forEachIndexed { index, pivotNode ->
                        val info = RenderableInfo(pivotNode.modelId, pivotNode.hash)
                        infoStates[info] = RenderableAnimationState(
                            name = pivotNode.name ?: "",
                            index = index,
                            initialPosition = pivotNode.position,
                            initialQuaternion = pivotNode.quaternion
                        )
                    }
                    onExternalEvent(ARSessionExternalEvent.OnLoadRenderables(infoStates))
                }

                val containerRenderableStates by remember(renderableInfoStates) {
                    derivedStateOf { renderableInfoStates.filter { it.key.modelId == model.id }.values }
                }

                LaunchedEffect(containerRenderableStates) {
                    containerNode.pivotNodes.forEach { pivotNode ->
                        val info = RenderableInfo(pivotNode.modelId, pivotNode.hash)
                        val state = renderableInfoStates[info] ?: return@forEach

                        pivotNode.renderableNode?.apply {
                            isVisible = state.isVisible
                            childNodes.forEach { it.isVisible = state.isVisible }

                            if (state.isSelectedToPlay) {
                                setPlayingMaterialInstance(materialLoader)
                            } else {
                                setUnselectedMaterialInstance(materialLoader)
                            }
                        }
                    }
                }
            }
        }
    }

    // Renderables States
    val selectedRenderablesStates by remember(renderableInfoStates) {
        derivedStateOf { renderableInfoStates.filter { it.value.isSelectedToPlay } }
    }

    val selectedOperation by remember(selectedOpTargets) {
        derivedStateOf { selectedOpTargets?.operation }
    }

    LaunchedEffect(
        selectedOperation,
        selectedRenderablesStates,
        options.isPlaying,
        options.currentSpeed,
        options.isLoopingEnabled,
        repositionChange
    ) {
        val currentOperation = selectedOperation

        applyOperationsOffsetsBeforeTo(
            steps = editorData.steps,
            operationsTargets = editorData.operationsTargets,
            currentOperation = currentOperation,
            renderableInfoStates = renderableInfoStates,
            containersMap = containersMap
        )

        if (currentOperation == null) return@LaunchedEffect

        launchOperationAnimation(
            currentOperation = currentOperation,
            operationsTargets = editorData.operationsTargets,
            renderableInfoStates = renderableInfoStates,
            selectedRenderableInfoStates = selectedRenderablesStates,
            containersMap = containersMap,
            isEditionEnabled = false,
            currentSpeed = options.currentSpeed,
            isPlaying = options.isPlaying,
            isLoopingEnabled = options.isLoopingEnabled,
        )

        if(!options.isLoopingEnabled) {
            onEvent(ARSessionEvent.OnSelectNextOperation)
        }
    }

    LaunchedEffect(options.isTorchEnabled) {
        session?.setTorch(options.isTorchEnabled)
    }

    LaunchedEffect(currentBitmapInfo) {
        val info = currentBitmapInfo ?: return@LaunchedEffect
        session?.setImageDatabase(info.markerId.toString(), info.bitmap) ?: return@LaunchedEffect
        nodes.safeTerminate(nodes.filterMarkerNodes())
    }

    SceneSurface(
        modifier = modifier,
        onNavigationUp = onNavigateBack,
        trailingAction = { rowHeight ->
            SceneMarkerIconButton(
                onClick = { showMarkersDialog = true },
                modifier = Modifier.size(rowHeight)
            )
        },
        optionsRow = { rowHeight ->
            OptionsRow(
                rowHeight = rowHeight,
                currentSpeed = options.currentSpeed,
                isLooping = options.isLoopingEnabled,
                isTorchEnabled = options.isTorchEnabled,
                onToggleTorch = { onEvent(ARSessionEvent.OnToggleTorch) },
                onToggleSpeed = { onEvent(ARSessionEvent.OnToggleSpeed) },
                onToggleLoop = { onEvent(ARSessionEvent.OnToggleLoop) },
            )
        },
        notificationChip = {
            var notificationMessage by remember { mutableStateOf<NotificationEvent?>(null) }
            LaunchedEffect(Unit) { notification.collectLatest { notificationMessage = it } }
            NotificationChip(
                event = notificationMessage,
                onDismiss = { notificationMessage = null }
            )
        }
    ) {
        ARScene(
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            sessionConfiguration = ::configureARSession,
            planeRenderer = false,
            cameraNode = cameraNode,
            childNodes = nodes,
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
            }
        )

        ShutterSection(
            shutterEnabled = isShutterEnabled,
            onClickShutter = { onEvent(ARSessionEvent.OnTogglePlay) },
            onPressShutter = {
                repositionOrigin()
                val infoToast = Toast.makeText(
                    context,
                    "Model repositioned to M${selectedMarker?.machineId}P${selectedMarker?.index}",
                    Toast.LENGTH_SHORT
                )
                infoToast.show()
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            leftSection = {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = MaterialTheme.spacing.small)
                ) {
                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectPreviousStep) },
                        icon = { StepIcon() },
                        content = {Text("Previous")}
                    )

                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectPreviousOperation) },
                        icon = { AnimationIcon() },
                        content = {Text("Previous")}
                    )
                }
            },
            rightSection = {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = MaterialTheme.spacing.small)
                ) {
                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectNextStep)},
                        iconInFront = false,
                        icon = { StepIcon() },
                        content = {Text("Next")}
                    )

                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectNextOperation)},
                        iconInFront = false,
                        icon = { AnimationIcon() },
                        content = {Text("Next")}
                    )
                }
            },
            iconDrawableId = if (options.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )

        if (showMarkersDialog) {
            SelectedMarkerDialog(
                modifier = Modifier,
                onDismissRequest = { showMarkersDialog = false },
                markers = editorData.markers,
                currentMarker = selectedMarker,
                onMarkerClick = { marker ->
                    onEvent(ARSessionEvent.OnSelectMarker(marker))
                    showMarkersDialog = false
                }
            )
        }
    }
}
