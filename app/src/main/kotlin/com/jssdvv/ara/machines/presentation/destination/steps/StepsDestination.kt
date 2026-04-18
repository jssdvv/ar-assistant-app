package com.jssdvv.ara.machines.presentation.destination.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.common.component.SearchIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.machines.domain.model.RenderableTarget
import com.jssdvv.ara.machines.domain.utility.PivotInfo
import com.jssdvv.ara.machines.domain.utility.applyOffsetBeforeTo
import com.jssdvv.ara.machines.domain.utility.launchOperationAnimation
import com.jssdvv.ara.machines.domain.utility.removeGizmoNodes
import com.jssdvv.ara.machines.domain.utility.renderableNode
import com.jssdvv.ara.machines.domain.utility.safeTerminate
import com.jssdvv.ara.machines.domain.utility.setSelectedMaterialInstance
import com.jssdvv.ara.machines.domain.utility.setUnselectedMaterialInstance
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.Speed
import com.jssdvv.ara.machines.presentation.destination.steps.component.AnimatedToolBar
import com.jssdvv.ara.machines.presentation.destination.steps.component.AnimationIcon
import com.jssdvv.ara.machines.presentation.destination.steps.component.DraggableDrawer
import com.jssdvv.ara.machines.presentation.destination.steps.component.OperationBottomSheet
import com.jssdvv.ara.machines.presentation.destination.steps.component.RenderableItem
import com.jssdvv.ara.machines.presentation.destination.steps.component.SearchBar
import com.jssdvv.ara.machines.presentation.destination.steps.component.StepIcon
import com.jssdvv.ara.machines.presentation.destination.steps.component.StepsSideSheet
import com.jssdvv.ara.machines.presentation.destination.steps.component.rememberDraggableDrawerState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.CustomCameraGestureDetector
import com.jssdvv.ara.machines.presentation.destination.steps.functions.rememberCustomCameraManipulator
import com.jssdvv.ara.machines.presentation.sceneview.node.PivotNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.createMainEnvironment
import com.jssdvv.ara.machines.presentation.sceneview.utility.findAncestorOrNull
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberCameraNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberLightNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberNodes
import io.github.sceneview.Scene
import io.github.sceneview.gesture.GestureDetector
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@Composable
fun StepsDestination(
    onNavigateUp: () -> Unit,
    viewModel: StepsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val renderableStates = viewModel.renderableInfoStates
    val isSelectionEnabled by viewModel.isSelectionEnabled.collectAsStateWithLifecycle()

    StepsScreen(
        uiState = uiState,
        renderableInfoStates = renderableStates,
        isSelectionEnabled = isSelectionEnabled,
        onEvent = viewModel::onEvent,
        onExternalEvent = viewModel::onExternalEvent,
        onNavigateUp = onNavigateUp
    )
}

@Composable
internal fun StepsScreen(
    uiState: StepsUiState,
    renderableInfoStates: SnapshotStateMap<RenderableInfo, RenderableState>,
    isSelectionEnabled: Boolean,
    onEvent: (StepsEvent) -> Unit,
    onExternalEvent: (StepsExternalEvent) -> Unit,
    onNavigateUp: () -> Unit,
) {
    when (uiState) {
        StepsUiState.EmptyModels -> {

        }

        StepsUiState.Loading -> LoadingWheel(Modifier.fillMaxSize())
        is StepsUiState.Success -> {
            StepsContent(
                models = uiState.models,
                steps = uiState.steps,
                operationsTargets = uiState.operationTargets,
                selectedStep = uiState.selectedStep,
                selectedOperation = uiState.selectedOp,
                renderableInfoStates = renderableInfoStates,
                isSelectionEnabled = isSelectionEnabled,
                onNavigateUp = onNavigateUp,
                onEvent = onEvent,
                onExternalEvent = onExternalEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsContent(
    models: List<Model>,
    steps: List<Step>,
    operationsTargets: List<OperationTargets>,
    selectedStep: Step?,
    selectedOperation: Operation?,
    renderableInfoStates: SnapshotStateMap<RenderableInfo, RenderableState>,
    isSelectionEnabled: Boolean,
    onEvent: (StepsEvent) -> Unit,
    onExternalEvent: (StepsExternalEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val view = rememberView(engine).apply { isStencilBufferEnabled = true }
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment = remember{ environmentLoader.createMainEnvironment() }

    // Nodes
    val nodes = rememberNodes()
    val containersMap by remember {
        derivedStateOf { nodes.filterContainerNodes().associateBy { it.modelId } }
    }
    val cameraNode = rememberCameraNode(engine)
    val mainLightNode = rememberNode { createMainLightNode(engine) }

    // Components Visibility
    val drawerState = rememberDraggableDrawerState()
    var showSideSheet by remember { mutableStateOf(false) }
    var isEditionEnabled by remember { mutableStateOf(false) } // To show bottom sheet

    val updatedEditionState by rememberUpdatedState(isEditionEnabled)
    val updatedSelectionState by rememberUpdatedState(isSelectionEnabled)

    // On Touch
    val cameraManipulator = rememberCustomCameraManipulator(cameraNode.worldPosition)
    val gestureListener = rememberOnGestureListener(
        onSingleTapConfirmed = { _, node ->
            if (updatedEditionState && updatedSelectionState) {
                node?.findPivotFromRenderable { pivotNode ->
                    val info = RenderableInfo(pivotNode.modelId, pivotNode.hash)
                    onExternalEvent(StepsExternalEvent.OnSelectRenderable(info))
                }
            }
        }
    )

    val gestureDetector = remember(gestureListener) { GestureDetector(context, gestureListener) }
    val gestureCameraDetector = remember(view, cameraManipulator) {
        CustomCameraGestureDetector({ view.viewport.height }, cameraManipulator)
    }

    models.forEach { model ->
        key(model.id) {
            val containerNode = remember(model.id) {
                createContainerNode(
                    engine = engine,
                    modelLoader = modelLoader,
                    materialLoader = materialLoader,
                    model = model,
                ).apply {
                    position = model.offsetPosition
                    quaternion = model.offsetRotation
                    childNodes.filterModelNodes().forEach { modelNode ->
                        modelNode.generatePivotNodes(materialLoader)
                    }
                }
            }

            DisposableEffect(containerNode) {
                nodes.add(containerNode)
                onDispose { nodes.safeTerminate(containerNode) }
            }

            LaunchedEffect(containerNode) {
                val infoStates = mutableMapOf<RenderableInfo, RenderableState>()
                containerNode.pivotNodes.forEachIndexed { index, pivotNode ->
                    val info = RenderableInfo(pivotNode.modelId, pivotNode.hash)
                    infoStates[info] = RenderableState(
                        name = pivotNode.name ?: "",
                        index = index,
                        initialPosition = pivotNode.position,
                        initialQuaternion = pivotNode.quaternion
                    )
                }
                onExternalEvent(StepsExternalEvent.OnLoadRenderables(infoStates))
            }

            val containerRenderableStates by remember(renderableInfoStates) {
                derivedStateOf { renderableInfoStates.filter { it.key.modelId == model.id }.values }
            }

            LaunchedEffect(containerRenderableStates) {
                containerNode.pivotNodes.forEach { pivotNode ->
                    val info = RenderableInfo(pivotNode.modelId, pivotNode.hash)
                    val state = renderableInfoStates[info] ?: return@forEach

                    pivotNode.apply {
                        if (state.isSelected && state.isVisible) {
                            if (gizmoNode == null) {
                                generateGizmoNode(materialLoader, selectedOperation?.isGlobal, true)
                            }
                        } else {
                            removeGizmoNodes()
                        }
                    }
                    pivotNode.boxNode?.apply { isVisible = state.isSelected }
                    pivotNode.renderableNode?.apply {
                        isVisible = state.isVisible
                        isTouchable = state.isVisible && !state.isSelected
                        childNodes.forEach { it.isVisible = state.isSelected }
                        if (state.isSelected) {
                            setSelectedMaterialInstance(materialLoader)
                        } else {
                            setUnselectedMaterialInstance(materialLoader)
                        }
                    }
                }
            }
        }
    }

    // Text field
    val focusManager = LocalFocusManager.current
    val textFieldState = rememberTextFieldState()
    val searchQuery by remember { derivedStateOf { textFieldState.text.toString().trim() } }
    val debouncedQuery by remember(textFieldState) { snapshotFlow { searchQuery }.debounce(200) }
        .collectAsStateWithLifecycle(initialValue = "")

    // Derived states of renderables
    val renderablesInfoItems by remember { derivedStateOf { renderableInfoStates.keys.toList() } }
    val selectedRenderableItems by remember(renderableInfoStates) {
        derivedStateOf { renderableInfoStates.filter { it.value.isSelected } }
    }

    // Items in the list
    val filteredRenderableItems by remember(renderablesInfoItems, debouncedQuery) {
        derivedStateOf {
            if (debouncedQuery.isBlank()) {
                renderablesInfoItems
            } else {
                renderablesInfoItems.filter {
                    renderableInfoStates[it]?.name?.contains(debouncedQuery, true) == true
                }
            }
        }
    }

    val operations = remember(operationsTargets) { operationsTargets.map { it.operation } }

    LaunchedEffect(selectedOperation, selectedRenderableItems, isEditionEnabled) {
        applyOperationsOffsetsBeforeTo(
            steps = steps,
            operationsTargets = operationsTargets,
            currentOperation = selectedOperation,
            renderableInfoStates = renderableInfoStates,
            containersMap = containersMap
        )

        if (selectedOperation == null) return@LaunchedEffect

        val isGlobal = selectedOperation.isGlobal

        if(updatedEditionState) {
            selectedRenderableItems.forEach { (info, state) ->
                val container = containersMap[info.modelId] ?: return@forEach
                val pivot = container.pivotNodes[state.index]
                pivot.gizmoNode?.apply {
                    if(isGlobal) {
                        worldQuaternion = Quaternion()
                    } else {
                        quaternion = Quaternion()
                    }
                }
            }
        }

        launchOperationAnimation(
            currentOperation = selectedOperation,
            operationsTargets = operationsTargets,
            renderableInfoStates = renderableInfoStates,
            selectedRenderableInfoStates = selectedRenderableItems,
            containersMap = containersMap,
            isEditionEnabled = isEditionEnabled,
            currentSpeed = Speed.NORMAL,
            isPlaying = true,
            isLoopingEnabled = true,
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.screen_editor_activity_title)) },
                navigationIcon = { NavigationUpIconButton(onNavigateUp) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                mainLightNode = mainLightNode,
                cameraNode = cameraNode,
                cameraManipulator = cameraManipulator,
                childNodes = nodes,
                onTouchEvent = { event, hitResult ->
                    gestureDetector.onTouchEvent(event, hitResult)
                    gestureCameraDetector.onTouchEvent(event)
                    true
                }
            )

            // Information Chips
            FlowRow(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.medium,
                    Alignment.End
                ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                maxItemsInEachRow = 3
            ) {
                ButtonWithIcon(
                    onClick = { showSideSheet = true },
                    icon = { StepIcon() },
                    content = {
                        Text(
                            text = pluralStringResource(
                                R.plurals.button_editor_step_count_label,
                                steps.size,
                                selectedStep?.order ?: 0,
                                steps.size
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                if (steps.isNotEmpty()) {
                    FilledIconButton(
                        onClick = { onEvent(StepsEvent.OnSelectPreviousStep) },
                        modifier = Modifier.size(40.dp),
                        enabled = selectedStep?.let { step ->
                            step.order > steps.minOf { it.order }
                        } ?: true,
                        content = { ArrowBackIcon() }
                    )

                    FilledIconButton(
                        onClick = { onEvent(StepsEvent.OnSelectNextStep) },
                        modifier = Modifier.size(40.dp),
                        enabled = selectedStep != null && selectedStep.order < steps.maxOf { it.order },
                        content = { ArrowBackIcon(Modifier.rotate(180F)) }
                    )
                }

                ButtonWithIcon(
                    onClick = { showSideSheet = true },
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    icon = { AnimationIcon() },
                    content = {
                        Text(
                            text = pluralStringResource(
                                R.plurals.button_editor_operation_count_label,
                                operations.size,
                                selectedOperation?.order ?: 0,
                                operations.size
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

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
                    SearchBar(
                        state = textFieldState,
                        onKeyboardAction = KeyboardActionHandler { focusManager.clearFocus() }
                    )
                    LazyColumn {
                        items(
                            items = filteredRenderableItems,
                            key = { "${it.modelId}_${it.xxh3}" }
                        ) { renderable ->

                            val renderableState by remember(renderable) {
                                derivedStateOf {
                                    renderableInfoStates[renderable] ?: RenderableState()
                                }
                            }

                            RenderableItem(
                                name = renderableState.name,
                                isVisible = renderableState.isVisible,
                                isSelected = false,
                                onVisibilityChange = {
                                    onExternalEvent(
                                        StepsExternalEvent.OnToggleRenderableVisibility(
                                            renderableInfo = renderable
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }

            StepsSideSheet(
                isVisible = showSideSheet,
                onDismiss = { showSideSheet = false },
                steps = steps,
                selectedStep = selectedStep,
                onNewStep = { onEvent(StepsEvent.OnNewStep) },
                onSaveStep = { onEvent(StepsEvent.OnSaveStep(it)) },
                onSelectStep = { onEvent(StepsEvent.OnSelectStep(it)) },
                onUpdateStep = { onEvent(StepsEvent.OnUpdateStep(it)) },
                operations = operations,
                selectedOperation = selectedOperation,
                onNewOperation = { stepId ->
                    isEditionEnabled = true
                    onEvent(StepsEvent.OnNewOperation(stepId))
                    showSideSheet = false
                },
                onEditOperation = { operationToEdit ->
                    isEditionEnabled = true
                    onEvent(StepsEvent.OnSelectOperation(operationToEdit.id))
                    val targets = operationsTargets
                        .find { it.operation.id == operationToEdit.id }
                        ?.targets
                        ?: emptyList()

                    onExternalEvent(StepsExternalEvent.OnSelectExistingRenderables(targets))
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
                        // Todo: dialog or bottomsheet to add operation
                        // todo fix this behaviour
                    },
                    enabled = isEditionEnabled,
                    content = { AnimationIcon() }
                )

                IconButton(
                    onClick = { showSideSheet = true },
                    enabled = !isEditionEnabled,
                    content = { StepIcon() }
                )
            }

            if (selectedStep != null && selectedOperation != null) {
                OperationBottomSheet(
                    isVisible = isEditionEnabled,
                    selectedRenderablesStates = selectedRenderableItems,
                    onUnselectItem = { onExternalEvent(StepsExternalEvent.OnUnselectRenderable(it)) },
                    onSelectionChange = {
                        onExternalEvent(StepsExternalEvent.OnChangeSelectionState(it))
                    },
                    onSaveClick = {
                        onEvent(StepsEvent.OnSaveOperation(it))
                        onExternalEvent(StepsExternalEvent.OnUnselectAllRenderables)
                        isEditionEnabled = false
                    },
                    onCancelClick = {
                        isEditionEnabled = false
                        onExternalEvent(StepsExternalEvent.OnUnselectAllRenderables)
                    },
                    selectedOperation = selectedOperation,
                    onOperationChange = { onEvent(StepsEvent.OnUpdateOperation(it)) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}