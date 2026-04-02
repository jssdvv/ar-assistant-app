package com.jssdvv.ara.machines.presentation.destination.steps

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.filament.LightManager
import com.google.android.filament.Skybox
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.ArrowBackIcon
import com.jssdvv.ara.core.presentation.common.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.common.SearchIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheel
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.utility.RenderableInfo
import com.jssdvv.ara.machines.presentation.destination.calibration.function.MODEL_SELECTED_COLOR
import com.jssdvv.ara.machines.presentation.destination.calibration.function.MODEL_UNSELECTED_COLOR
import com.jssdvv.ara.machines.presentation.destination.calibration.function.createModelNode
import com.jssdvv.ara.machines.presentation.destination.calibration.function.getModelMaterialInstance
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
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.Scene
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR_INTENSITY
import io.github.sceneview.gesture.GestureDetector
import io.github.sceneview.managers.color
import io.github.sceneview.math.Position
import io.github.sceneview.math.halfExtentSize
import io.github.sceneview.math.toFloat3
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.LightNode
import io.github.sceneview.node.ModelNode
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
import net.openhft.hashing.LongHashFunction

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
                operationTargets = uiState.operationTargets,
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
    operationTargets: List<OperationTargets>,
    selectedStep: Step?,
    selectedOperation: Operation?,
    renderableInfoStates: SnapshotStateMap<RenderableInfo, RenderableState>,
    isSelectionEnabled: Boolean,
    onEvent: (StepsEvent) -> Unit,
    onExternalEvent: (StepsExternalEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Scene
    val engine = rememberEngine()
    val view = rememberView(engine).apply { isStencilBufferEnabled = true }
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment = environmentLoader.createEnvironment(
        skybox = Skybox
            .Builder()
            .color(0.2F, 0.2F, 0.2F, 1.0F)
            .build(engine)
    )

    // Nodes
    val nodes = rememberNodes()
    val modelNodes by remember { derivedStateOf { nodes.filterIsInstance<ModelNode>() } }
    val modelNodeMap by remember {
        derivedStateOf { nodes.filterIsInstance<ModelNode>().associateBy { it.name } }
    }
    val cameraNode = rememberCameraNode(engine)
    val mainLightNode = rememberNode {
        LightNode(
            engine = engine,
            type = LightManager.Type.DIRECTIONAL,
            apply = {
                color(DEFAULT_MAIN_LIGHT_COLOR)
                intensity(DEFAULT_MAIN_LIGHT_COLOR_INTENSITY)
                direction(0F, -1F, -1F)
                castShadows(true)
            }
        )
    }

    // On Touch
    val cameraManipulator = rememberCustomCameraManipulator(cameraNode.worldPosition)
    val gestureDetector = GestureDetector(LocalContext.current, rememberOnGestureListener())
    val gestureCameraDetector = remember(view, cameraManipulator) {
        CustomCameraGestureDetector(
            viewHeight = { view.viewport.height },
            cameraManipulator = cameraManipulator
        )
    }

    // Components Visibility
    val drawerState = rememberDraggableDrawerState()
    var showSideSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }


    models.forEach { model ->
        key(model.id) {
            val modelNode = remember(model.id) {
                createModelNode(
                    engine = engine,
                    modelLoader = modelLoader,
                    materialLoader = materialLoader,
                    modelFile = model.glbUri.toFile(),
                    modelId = model.id
                ).apply {
                    isHittable = false
                    isTouchable = false
                    position = model.offsetPosition
                    quaternion = model.offsetRotation

                    renderableNodes.forEach { renderableNode ->
                        val boundingBox = renderableNode.axisAlignedBoundingBox
                        val cubeNode = CubeNode(
                            engine = engine,
                            size = boundingBox.halfExtentSize * Float3(2F) + Float3(0.005F),
                            center = boundingBox.center.toFloat3(),
                            materialInstance = materialLoader.createColorInstance(
                                color = Color(1F, 1F, 1F, 0.2F),
                                metallic = 0.1F,
                                reflectance = 1F
                            )
                        ).apply {
                            isVisible = false
                            isTouchable = false
                        }
                        renderableNode.updateCollisionShape()
                        renderableNode.addChildNode(cubeNode)
                    }
                }
            }

            DisposableEffect(modelNode) {
                nodes.add(modelNode)
                onDispose {
                    nodes.remove(modelNode)
                    modelNode.destroy()
                }
            }

            val isSelectionEnabledState = rememberUpdatedState(isSelectionEnabled)
            val showBottomSheetState = rememberUpdatedState(showBottomSheet)

            modelNode.renderableNodes.forEachIndexed { tempIndex, renderableNode ->

                val hash = remember(renderableNode.name) {
                    LongHashFunction.xx3().hashChars(renderableNode.name ?: "")
                }

                val renderableInfo = remember(model.id, hash) {
                    RenderableInfo(model.id, hash)
                }

                val initialState = remember(model.id, hash) {
                    RenderableState(
                        name = renderableNode.name ?: "",
                        tempIndex = tempIndex,
                        initialPosition = renderableNode.position,
                        initialQuaternion = renderableNode.quaternion
                    )
                }

                LaunchedEffect(renderableInfo) {
                    onExternalEvent(StepsExternalEvent.OnLoadRenderables(renderableInfo, initialState))

                    renderableNode.onSingleTapConfirmed = { _ ->
                        if (showBottomSheetState.value && isSelectionEnabledState.value) {
                            onExternalEvent(StepsExternalEvent.OnSelectRenderable(renderableInfo))
                        }
                        false
                    }
                }

                val state by remember(renderableInfo) {
                    derivedStateOf { renderableInfoStates[renderableInfo] ?: initialState }
                }

                LaunchedEffect(state.isVisible, state.isSelected) {
                    renderableNode.isVisible = state.isVisible
                    renderableNode.isTouchable = state.isVisible && !state.isSelected
                    renderableNode.childNodes.forEach { it.isVisible = state.isSelected }
                    renderableNode.materialInstance = getModelMaterialInstance(
                        materialLoader = materialLoader,
                        modelColor = if (state.isSelected) MODEL_SELECTED_COLOR else MODEL_UNSELECTED_COLOR
                    )
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
    val renderablesItems by remember { derivedStateOf { renderableInfoStates.keys.toList() } }
    val selectedRenderableItems by remember(renderableInfoStates) {
        derivedStateOf { renderableInfoStates.filter { it.value.isSelected } }
    }
    val filteredRenderableItems by remember(renderablesItems, debouncedQuery) {
        derivedStateOf {
            if (debouncedQuery.isBlank()) {
                renderablesItems
            } else {
                renderablesItems.filter {
                    renderableInfoStates[it]?.name?.contains(debouncedQuery, true) == true
                }
            }
        }
    }

    val operations = operationTargets.map { it.operation }

    val activeAnimators = remember { mutableListOf<AnimatorSet>() }

    fun restoreAndApplyOperationUpTo(
        operation: Operation?,
        renderableInfos: List<RenderableInfo>,
    ) {
        // Cancel previous animators
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()

        // Restore position and rotation of all renderables of all models
        renderableInfoStates.forEach { (renderable, state) ->
            val modelNode = modelNodeMap[renderable.modelId.toString()] ?: return@forEach
            modelNode.renderableNodes.getOrNull(state.tempIndex)?.apply{
                position = state.initialPosition
                quaternion = state.initialQuaternion
            }
        }

        // If there is no operation, there is no offsets applied
        if (operation == null) return

        val isNewOperation = operation.id == 0

        // returns a step orderNumber of given step id
        val stepOrderNumberMap = steps.associate { it.id to it.order }

        // An operation should have first a step Id
        val targetStepOrder = stepOrderNumberMap[operation.stepId] ?: return

        val operationTargetsBeforeCurrent = operationTargets
            .filter { stepOrderNumberMap[it.operation.stepId] != null }
            .sortedWith(
                compareBy(
                    { stepOrderNumberMap[it.operation.stepId] },
                    { it.operation.order }
                )
            )
            .takeWhile { operationTarget ->
                val stepOrder =
                    stepOrderNumberMap[operationTarget.operation.stepId] ?: return@takeWhile false

                if (operationTarget.operation.id == 0) return@takeWhile false

                stepOrder < targetStepOrder ||
                (stepOrder == targetStepOrder && isNewOperation) ||
                (stepOrder == targetStepOrder && operationTarget.operation.order < operation.order)
            }

        // Apply offsets until current operation
        operationTargetsBeforeCurrent.forEach { operationTarget ->
            operationTarget.targets.forEach { target ->
                val modelNode = modelNodeMap[target.modelId.toString()] ?: return@forEach
                val renderableInfo = RenderableInfo(target.modelId, target.xxh3)
                val state = renderableInfoStates[renderableInfo] ?: return@forEach

                modelNode.renderableNodes.getOrNull(state.tempIndex)?.apply {
                    // Rotate with it's local quaternion of rotation
                    val rotatedOffsetPosition = quaternion * operationTarget.operation.offsetPosition
                    position += rotatedOffsetPosition
                    quaternion = normalize(quaternion * operationTarget.operation.offsetRotation)
                }
            }
        }

        // Apply offsets to all renderables of the current operation
        val currentOperationTargets = operationTargets.find { it.operation.id == operation.id }

        currentOperationTargets?.targets?.forEach { target ->
            val modelNode = modelNodeMap[target.modelId.toString()] ?: return@forEach
            val renderableInfo = RenderableInfo(target.modelId, target.xxh3)
            val state = renderableInfoStates[renderableInfo] ?: return@forEach

            modelNode.renderableNodes.getOrNull(state.tempIndex)?.apply {

                val initialPosition = position
                val initialQuaternion = quaternion
                val rotatedOffsetPosition = initialQuaternion * operation.offsetPosition

                val finalPosition = initialPosition + rotatedOffsetPosition
                val finalQuaternion = normalize(initialQuaternion * operation.offsetRotation)

                val positionAnimator = animatePositions(
                    initialPosition,
                    finalPosition
                ).apply{
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    addUpdateListener {
                        val x = it.getAnimatedValue("x") as Float
                        val y = it.getAnimatedValue("y") as Float
                        val z = it.getAnimatedValue("z") as Float
                        position = Position(x, y, z)
                    }
                }
                val quaternionAnimator = animateQuaternions(
                    initialQuaternion,
                    finalQuaternion
                ).apply {
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    addUpdateListener {
                        val x = it.getAnimatedValue("x") as Float
                        val y = it.getAnimatedValue("y") as Float
                        val z = it.getAnimatedValue("z") as Float
                        val w = it.getAnimatedValue("w") as Float
                        quaternion = Quaternion(x, y, z, w)
                    }
                }

                val animatorSet = AnimatorSet().apply {
                    playTogether(positionAnimator, quaternionAnimator)
                    startDelay = operation.delay.toLong() * 1_000L
                    duration = operation.duration.toLong() * 1_000L
                    start()
                }
                activeAnimators.add(animatorSet)
            }
        }
    }

    LaunchedEffect(selectedOperation, selectedRenderableItems) {
        restoreAndApplyOperationUpTo(selectedOperation, selectedRenderableItems.keys.toList())
    }

    DisposableEffect(Unit) {
        onDispose {
            activeAnimators.forEach { it.cancel() }
            activeAnimators.clear()
        }
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
                    gestureCameraDetector.onTouchEvent(event)
                    gestureDetector.onTouchEvent(event, hitResult)
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
                        val stepText = if (steps.isEmpty()) {
                            stringResource(R.string.button_editor_no_steps_label)
                        } else {
                            stringResource(
                                R.string.button_editor_step_count_label,
                                selectedStep?.order ?: 0,
                                steps.size
                            )
                        }

                        Text(
                            text = stepText,
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
                    onClick = {
                        showSideSheet = true
                        // todo add expand current step to op
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    icon = { AnimationIcon() },
                    content = {
                        val opText = if (operations.isEmpty()) {
                            stringResource(R.string.button_editor_no_operations_label)
                        } else {
                            stringResource(
                                R.string.button_editor_operation_count_label,
                                selectedOperation?.order ?: 0,
                                operations.size
                            )
                        }
                        Text(
                            text = opText,
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
                                derivedStateOf { renderableInfoStates[renderable] ?: RenderableState() }
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
                    showBottomSheet = true
                    onEvent(StepsEvent.OnNewOperation(stepId))
                    showSideSheet = false
                },
                onEditOperation = { operationToEdit ->
                    showBottomSheet = true
                    onEvent(StepsEvent.OnSelectOperation(operationToEdit.id))
                    val targets = operationTargets
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
                    enabled = showBottomSheet,
                    content = { AnimationIcon() }
                )

                IconButton(
                    onClick = { showSideSheet = true },
                    enabled = !showBottomSheet,
                    content = { StepIcon() }
                )
            }

            if (selectedStep != null && selectedOperation != null) {
                OperationBottomSheet(
                    isVisible = showBottomSheet,
                    selectedRenderablesStates = selectedRenderableItems,
                    onUnselectItem = { onExternalEvent(StepsExternalEvent.OnUnselectRenderable(it)) },
                    onSelectionChange = {
                        onExternalEvent(StepsExternalEvent.OnToggleSelection(it))
                    },
                    onSaveClick = {
                        onEvent(StepsEvent.OnSaveOperation(it))
                        onExternalEvent(StepsExternalEvent.OnUnselectAllRenderables)
                        showBottomSheet = false
                    },
                    onCancelClick = {
                        showBottomSheet = false
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