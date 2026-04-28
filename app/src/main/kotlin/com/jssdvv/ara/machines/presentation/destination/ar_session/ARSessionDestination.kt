package com.jssdvv.ara.machines.presentation.destination.ar_session

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Session
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.DeniedPermissions
import com.jssdvv.ara.core.presentation.common.component.PermanentlyDeniedPermissions
import com.jssdvv.ara.core.presentation.common.state.Permission
import com.jssdvv.ara.core.presentation.common.state.allGranted
import com.jssdvv.ara.core.presentation.common.state.anyDenied
import com.jssdvv.ara.core.presentation.common.state.anyPermanentlyDenied
import com.jssdvv.ara.core.presentation.common.state.deniedPermissions
import com.jssdvv.ara.core.presentation.common.state.permanentlyDeniedRationales
import com.jssdvv.ara.core.presentation.foundation.component.ARSceneSurface
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.presentation.component.NotificationChip
import com.jssdvv.ara.machines.presentation.component.SceneMarkerIconButton
import com.jssdvv.ara.machines.presentation.component.ShutterSection
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.OptionsRow
import com.jssdvv.ara.machines.presentation.destination.calibration.component.SelectedMarkerDialog
import com.jssdvv.ara.machines.presentation.destination.steps.component.AnimationIcon
import com.jssdvv.ara.machines.presentation.destination.steps.component.StepIcon
import com.jssdvv.ara.machines.presentation.sceneview.utility.PivotNodesMap
import com.jssdvv.ara.machines.presentation.sceneview.utility.applyOffset
import com.jssdvv.ara.machines.presentation.sceneview.utility.configureARSession
import com.jssdvv.ara.machines.presentation.sceneview.utility.detectMarkerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.markerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberNodes
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberOriginNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.safeTerminate
import com.jssdvv.ara.machines.presentation.sceneview.utility.setImageDatabase
import com.jssdvv.ara.machines.presentation.sceneview.utility.setTorch
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun ARSessionDestination(
    onNavigateUp: () -> Unit,
    viewModel: ARSessionViewModel = hiltViewModel(),
) = ARCameraScreen(
    notification = viewModel.notification,
    permissions = viewModel.permissions.collectAsStateWithLifecycle().value,
    uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
    onEvent = viewModel::onEvent,
    onNavigateUp = onNavigateUp,
)

@Composable
internal fun ARCameraScreen(
    notification: SharedFlow<NotificationEvent>,
    permissions: Set<Permission>,
    uiState: ARSessionUiState,
    onEvent: (ARSessionEvent) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val activity = LocalActivity.current

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onEvent(ARSessionEvent.OnCheckPermissionsStates) }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        permissionsResult.forEach {
            onEvent(ARSessionEvent.OnPermissionInteraction(it.key))
            activity?.shouldShowRequestPermissionRationale(it.key)
        }
        onEvent(ARSessionEvent.OnCheckPermissionsStates)
    }

    when {
        permissions.allGranted() -> {
            when (uiState) {
                ARSessionUiState.Loading -> LoadingWheelScreen()

                is ARSessionUiState.Success -> {
                    ARCameraContent(
                        notification = notification,
                        data = uiState.data,
                        items = uiState.items,
                        animation = uiState.animation,
                        options = uiState.options,
                        onEvent = onEvent,
                        onNavigateUp = onNavigateUp
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

@Composable
fun ARCameraContent(
    notification: SharedFlow<NotificationEvent>,
    data: ARSessionData,
    items: ARSessionItems,
    animation: ARSessionAnimation,
    options: ARSessionOptions,
    onEvent: (ARSessionEvent) -> Unit,
    onNavigateUp: () -> Unit,
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
    val camera = rememberARCameraNode(engine)
    val marker = remember(nodes.size) { nodes.markerNode }

    // Renderables Lookup
    val pivotNodesMap: PivotNodesMap = remember { mutableStateMapOf() }

    var showMarkersDialog by remember { mutableStateOf(true) }
    var showShutterSection by remember { mutableStateOf(true) }

    if (marker != null) {
        data.models.forEach { model ->
            key(model.id) {
                rememberContainerNode(model, engine) {
                    setModelNode(modelLoader, materialLoader)
                    generatePivotNodes()
                }.also { container ->
                    DisposableEffect(container) {
                        origin.addChildNode(container)
                        onEvent(ARSessionEvent.OnRepositionOrigin(origin, marker))
                        onDispose {
                            nodes.remove(container)
                            container.safeTerminate()
                        }
                    }
                    LaunchedEffect(container) {
                        container.pivotNodes.associateByTo(pivotNodesMap) { it.pivot }
                    }
                }
            }
        }
    }

    var previousTransformedPivots by remember { mutableStateOf(emptySet<Pivot>()) }
    LaunchedEffect(animation, items.currentOperation, options) {
        val transformedPivots: Set<Pivot> = animation.transformedTargets
            .flatMapTo(mutableSetOf(), OperationTargets::pivots)

        val animatedPivots = animation.animatedPivots

        previousTransformedPivots.forEach { pivotNodesMap[it]?.restoreTransform() }
        previousTransformedPivots = transformedPivots + animatedPivots

        // Calculate offset of transformed targets pivots
        animation.transformedTargets.forEach { opTargets ->
            val operation = opTargets.operation
            opTargets.pivots.forEach { pivot ->
                pivotNodesMap[pivot]?.applyOffset(operation.offsetTransform, operation.global)
            }
        }

        val currentOperation = items.currentOperation ?: return@LaunchedEffect
        val nodesToAnimate = animatedPivots.mapNotNull { pivotNodesMap[it] }
        coroutineScope {
            nodesToAnimate.map {
                launch {
                    it.animate(
                        operation = currentOperation,
                        speed = options.speed,
                        playing = options.playing,
                        looping = options.looping
                    )
                }
            }.joinAll()
        }
    }

    LaunchedEffect(items.currentBitmap, Unit) {
        val info = items.currentBitmap ?: return@LaunchedEffect
        session?.setImageDatabase(info.markerId.toString(), info.bitmap)
    }

    LaunchedEffect(options.torchEnabled) {
        session?.setTorch(options.torchEnabled)
    }

    ARSceneSurface(
        onNavigationUp = onNavigateUp,
        trailingAction = { SceneMarkerIconButton(Modifier.size(it)) { showMarkersDialog = true } },
        optionsRow = { rowHeight ->
            OptionsRow(
                rowHeight = rowHeight,
                currentSpeed = options.speed,
                isLooping = options.looping,
                isTorchEnabled = options.torchEnabled,
                onToggleTorch = { onEvent(ARSessionEvent.OnToggleTorch) },
                onToggleSpeed = { onEvent(ARSessionEvent.OnToggleSpeed) },
                onToggleLoop = { onEvent(ARSessionEvent.OnToggleLoop) },
            )
        },
        notificationChip = {
            var event by remember { mutableStateOf<NotificationEvent?>(null) }
            LaunchedEffect(Unit) { notification.collectLatest { event = it } }
            NotificationChip(event) { event = null }
        }
    ) {
        ARScene(
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            sessionConfiguration = ::configureARSession,
            planeRenderer = false,
            cameraNode = camera,
            childNodes = nodes,
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
                                onEvent(ARSessionEvent.OnRepositionOrigin(origin, marker))
                            }
                        )
                    }
                }
            }
        )

        ShutterSection(
            visible = showShutterSection,
            shutterClickEnabled = true,
            shutterPressEnabled = isShutterEnabled,
            onClickShutter = { onEvent(ARSessionEvent.OnTogglePlay) },
            onPressShutter = { onEvent(ARSessionEvent.OnRepositionOrigin(origin, marker)) },
            modifier = Modifier.align(Alignment.BottomCenter),
            leftSection = {
                Column(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectPreviousStep) },
                        icon = { StepIcon(Modifier.graphicsLayer { rotationY = 180F }) },
                        content = { Text("Previous") }
                    )

                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectPreviousOperation) },
                        icon = { AnimationIcon(Modifier.graphicsLayer { rotationY = 180F }) },
                        content = { Text("Previous") }
                    )
                }
            },
            rightSection = {
                Column(
                    modifier = Modifier.padding(end = MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectNextStep) },
                        iconInFront = false,
                        icon = { StepIcon() },
                        content = { Text("Next") }
                    )

                    ButtonWithIcon(
                        onClick = { onEvent(ARSessionEvent.OnSelectNextOperation) },
                        iconInFront = false,
                        icon = { AnimationIcon() },
                        content = { Text("Next") }
                    )
                }
            },
            iconDrawableId = if (options.playing) R.drawable.ic_pause else R.drawable.ic_play
        )

        if (showMarkersDialog) {
            SelectedMarkerDialog(
                modifier = Modifier,
                onDismissRequest = { showMarkersDialog = false },
                markers = data.markers,
                currentMarker = items.selectedMarker,
                onMarkerClick = { marker ->
                    onEvent(ARSessionEvent.OnSelectMarker(marker))
                    showMarkersDialog = false
                }
            )
        }
    }
}
