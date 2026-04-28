package com.jssdvv.ara.machines.presentation.destination.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.common.component.SearchIcon
import com.jssdvv.ara.core.presentation.foundation.component.BoxedScaffold
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.presentation.destination.steps.component.AnimatedToolBar
import com.jssdvv.ara.machines.presentation.destination.steps.component.AnimationIcon
import com.jssdvv.ara.machines.presentation.destination.steps.component.DraggableDrawer
import com.jssdvv.ara.machines.presentation.destination.steps.component.InformationChips
import com.jssdvv.ara.machines.presentation.destination.steps.component.OperationBottomSheet
import com.jssdvv.ara.machines.presentation.destination.steps.component.RenderableItem
import com.jssdvv.ara.machines.presentation.destination.steps.component.SearchBar
import com.jssdvv.ara.machines.presentation.destination.steps.component.StepIcon
import com.jssdvv.ara.machines.presentation.destination.steps.component.StepsSideSheet
import com.jssdvv.ara.machines.presentation.destination.steps.component.rememberDraggableDrawerState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.CustomCameraGestureDetector
import com.jssdvv.ara.machines.presentation.destination.steps.functions.rememberCustomCameraManipulator
import com.jssdvv.ara.machines.presentation.sceneview.node.PivotNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.PivotNodesMap
import com.jssdvv.ara.machines.presentation.sceneview.utility.applyOffset
import com.jssdvv.ara.machines.presentation.sceneview.utility.createMainEnvironment
import com.jssdvv.ara.machines.presentation.sceneview.utility.findAncestorOrNull
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberCameraNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberLightNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberNodes
import com.jssdvv.ara.machines.presentation.sceneview.utility.safeTerminate
import io.github.sceneview.Scene
import io.github.sceneview.gesture.GestureDetector
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
fun StepsDestination(
    onNavigateUp: () -> Unit,
    viewModel: StepsViewModel = hiltViewModel(),
) = StepsScreen(
    uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
    onEvent = viewModel::onEvent,
    onNavigateUp = onNavigateUp
)

@Composable
internal fun StepsScreen(
    uiState: StepsUiState,
    onEvent: (StepsEvent) -> Unit,
    onNavigateUp: () -> Unit,
) {
    when (uiState) {
        StepsUiState.Loading -> LoadingWheelScreen()
        StepsUiState.EmptyModels -> {}

        is StepsUiState.Success -> {
            StepsContent(
                data = uiState.data,
                items = uiState.items,
                animation = uiState.animation,
                onNavigateUp = onNavigateUp,
                onEvent = onEvent,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun StepsContent(
    data: StepsData,
    items: StepsItems,
    animation: StepsAnimation,
    onEvent: (StepsEvent) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val view = rememberView(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment = remember(environmentLoader::createMainEnvironment)

    // The Scene Nodes
    val nodes = rememberNodes()
    val camera = rememberCameraNode(engine)
    val light = rememberLightNode(engine)

    // Renderables Lookup
    val pivotNodesMap: PivotNodesMap = remember { mutableStateMapOf() }

    // Components Visibility
    val drawerState = rememberDraggableDrawerState()
    var showSideSheet by remember { mutableStateOf(false) }

    val updatedSelectionEnabled by rememberUpdatedState(items.selectionEnabled)

    // On Touch
    val cameraManipulator = rememberCustomCameraManipulator(camera.worldPosition)
    val gestureListener = rememberOnGestureListener(
        onSingleTapConfirmed = { _, node ->
            if (!updatedSelectionEnabled) return@rememberOnGestureListener
            node?.findAncestorOrNull<PivotNode>()
                ?.apply { onEvent(StepsEvent.OnSelectPivots(pivot)) }
        }
    )

    val gestureDetector = remember(gestureListener) { GestureDetector(context, gestureListener) }
    val gestureCameraDetector = remember(view, cameraManipulator) {
        CustomCameraGestureDetector({ view.viewport.height }, cameraManipulator)
    }

    data.models.forEach { model ->
        key(model.id) {
            rememberContainerNode(model, engine) {
                setModelNode(modelLoader, materialLoader)
                generatePivotNodes()
            }.also { container ->
                DisposableEffect(container) {
                    nodes.add(container)
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

    val textFieldState = rememberTextFieldState()
    val searchQuery by remember { derivedStateOf { textFieldState.text.toString().trim() } }
    val debouncedQuery by remember(textFieldState) {
        snapshotFlow { searchQuery }.debounce(200)
    }.collectAsStateWithLifecycle(initialValue = "")

    val filteredPivots by remember(debouncedQuery, pivotNodesMap.size) {
        derivedStateOf {
            val allPivots = pivotNodesMap.values.toList()
            if (debouncedQuery.isBlank()) {
                allPivots.sortedBy { it.renderableNode?.name }
            } else {
                allPivots.filter {
                    it.renderableNode?.name?.contains(debouncedQuery, ignoreCase = true) == true
                }.sortedBy { it.renderableNode?.name }
            }
        }
    }

    val operations = remember(data.operationsTargets) {
        data.operationsTargets.map { it.operation }
    }

    var previousAddedPivots by remember { mutableStateOf(setOf<Pivot>()) }
    var previousAnimatedPivots by remember { mutableStateOf(setOf<Pivot>()) }
    LaunchedEffect(
        animation.animatedPivots,
        items.editionEnabled,
        items.currentOperation?.id,
        items.currentOperation?.global
    ) {
        if (items.currentOperation?.id == 0 && animation.animatedPivots.isEmpty()) {
            previousAnimatedPivots.forEach {
                pivotNodesMap[it]?.setSelection(
                    false,
                    materialLoader
                )
            }
        }

        previousAddedPivots = if (items.editionEnabled) {
            val animatedPivots = animation.animatedPivots

            // Removed
            (previousAddedPivots - animatedPivots).forEach {
                pivotNodesMap[it]?.setSelection(false, materialLoader)
            }

            // Added
            (animatedPivots - previousAddedPivots).forEach {
                pivotNodesMap[it]?.setSelection(true, materialLoader)
            }

            animatedPivots
        } else {
            animation.animatedPivots.forEach { pivotNodesMap[it]?.setPlaying(materialLoader) }
            emptySet()
        }
        previousAnimatedPivots = animation.animatedPivots
    }

    var previousTransformedPivots by remember { mutableStateOf(emptySet<Pivot>()) }
    LaunchedEffect(animation, items.currentOperation) {
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
        val nodesToAnimate = animatedPivots.mapNotNull(pivotNodesMap::get)
        val global = currentOperation.global

        coroutineScope {
            nodesToAnimate
                .onEach { it.updateGizmoOrientation(global) }
                .map { launch { it.animate(currentOperation) } }.joinAll()
        }
    }

    BoxedScaffold(
        topBarTitle = stringResource(R.string.screen_editor_activity_title),
        navigationIcon = { NavigationUpIconButton(onNavigateUp) }
    ) {
        Scene(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            engine = engine,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            environmentLoader = environmentLoader,
            view = view,
            isOpaque = true,
            environment = environment,
            mainLightNode = light,
            cameraNode = camera,
            cameraManipulator = cameraManipulator,
            childNodes = nodes,
            onTouchEvent = { event, hitResult ->
                gestureDetector.onTouchEvent(event, hitResult)
                gestureCameraDetector.onTouchEvent(event)
                true
            }
        )

        InformationChips(
            currentStepOrder = items.currentStep?.order ?: 0,
            currentOperationOrder = items.currentOperation?.order ?: 0,
            stepsCount = data.steps.size,
            operationsCount = data.operationsTargets.size,
            modifier = Modifier.align(Alignment.TopEnd),
        )

        // Renderables List
        DraggableDrawer(
            state = drawerState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
                horizontalAlignment = Alignment.Start
            ) {
                SearchBar(state = textFieldState)
                LazyColumn {
                    items(
                        items = filteredPivots,
                        key = { "${it.pivot.modelId}_${it.pivot.xxh3}" }
                    ) {
                        RenderableItem(
                            name = it.renderableNode?.name ?: "Unknown",
                            isVisible = it.renderableVisible,
                            isSelected = animation.animatedPivots.contains(it.pivot),
                            onVisibilityChange = { it.toggleVisibility() }
                        )
                    }
                }
            }
        }

        StepsSideSheet(
            visible = showSideSheet && items.currentOperation?.id != 0,
            steps = data.steps,
            operations = operations,
            editingStep = items.currentStep,
            currentStep = items.currentStep,
            currentOperation = items.currentOperation,
            onDismiss = { showSideSheet = false },
            onEditStep = { onEvent(StepsEvent.OnEditStep(it)) },
            onCreateStep = { onEvent(StepsEvent.OnCreateStep) },
            onSaveEditingStep = { onEvent(StepsEvent.OnSaveEditingStep) },
            onChangeEditingStep = { onEvent(StepsEvent.OnChangeEditingStep(it)) },
            onCreateOperation = { stepId ->
                onEvent(StepsEvent.OnCreateOperation(stepId))
                showSideSheet = false
            },
            onEditOperation = { operation ->
                onEvent(StepsEvent.OnEditOperation(operation.id))
                showSideSheet = false
            },
            onSelectOperation = { onEvent(StepsEvent.OnSelectOperation(it)) }
        )

        AnimatedToolBar(
            isVisible = !showSideSheet,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            IconButton(
                onClick = { scope.launch { drawerState.toggle() } },
                content = { SearchIcon() }
            )
            IconButton(
                onClick = {
                    onEvent(StepsEvent.OnEditCurrentOperation)
                    showSideSheet = false
                },
                content = { AnimationIcon() }
            )
            IconButton(
                onClick = { showSideSheet = true },
                content = { StepIcon() }
            )
        }

        if (items.editingOperation != null) {
            OperationBottomSheet(
                visible = items.editionEnabled,
                selectedPivots = animation.animatedPivots.mapNotNullTo(
                    mutableSetOf(),
                    pivotNodesMap::get
                ),
                onUnselectPivot = { onEvent(StepsEvent.OnUnselectPivot(it)) },
                onSelectionChange = { onEvent(StepsEvent.OnSelectionChange(it)) },
                editingOperation = items.editingOperation,
                onChangeEditingOperation = { onEvent(StepsEvent.OnChangeEditingOperation(it)) },
                onSaveEditingOperation = { onEvent(StepsEvent.OnSaveEditingOperation) },
                onCancelEditingOperation = { onEvent(StepsEvent.OnCancelEditingOperation) },
                onDeleteEditingOperation = {}, // TODO add this
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}