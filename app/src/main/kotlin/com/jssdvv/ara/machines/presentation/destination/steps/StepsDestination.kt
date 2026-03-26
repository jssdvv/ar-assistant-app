package com.jssdvv.ara.machines.presentation.destination.steps

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.filament.LightManager
import com.google.android.filament.Skybox
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
import io.github.sceneview.Scene
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR_INTENSITY
import io.github.sceneview.gesture.GestureDetector
import io.github.sceneview.managers.color
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

@Composable
fun StepsDestination(
    onNavigateUp: () -> Unit,
    viewModel: StepsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val renderableStates = viewModel.renderableStates
    val isSelectionEnabled by viewModel.isSelectionEnabled.collectAsStateWithLifecycle()

    StepsScreen(
        uiState = uiState,
        renderableStates = renderableStates,
        isSelectionEnabled = isSelectionEnabled,
        onEvent = viewModel::onEvent,
        onExternalEvent = viewModel::onExternalEvent,
        onNavigateUp = onNavigateUp
    )
}

@Composable
internal fun StepsScreen(
    uiState: StepsUiState,
    renderableStates: SnapshotStateMap<Renderable, RenderableState>,
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
                renderableStates = renderableStates,
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
    renderableStates: SnapshotStateMap<Renderable, RenderableState>,
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
                    position = model.positionFromOrigin
                    quaternion = model.rotationFromOrigin

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

            modelNode.renderableNodes.forEachIndexed { index, renderableNode ->

                val renderable = remember(model.id, index) {
                    Renderable(
                        modelId = model.id,
                        index = index,
                        name = renderableNode.name ?: "pieza $index",
                        initialPosition = renderableNode.position,
                        initialQuaternion = renderableNode.quaternion
                    )
                }

                LaunchedEffect(renderable) {
                    onExternalEvent(StepsExternalEvent.OnLoadRenderables(renderable))

                    renderableNode.onSingleTapConfirmed = { _ ->
                        if (showBottomSheetState.value && isSelectionEnabledState.value) {
                            onExternalEvent(StepsExternalEvent.OnSelectRenderable(renderable))
                        }
                        false
                    }
                }

                val state by remember(renderable) {
                    derivedStateOf { renderableStates[renderable] ?: RenderableState() }
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
    val renderablesItems = renderableStates.keys.toList()
    val selectedRenderableItems by remember(renderableStates) {
        derivedStateOf { renderableStates.filter { it.value.isSelected }.keys.toList() }
    }
    val filteredRenderableItems by remember(renderablesItems, debouncedQuery) {
        derivedStateOf {
            if (debouncedQuery.isBlank()) {
                renderablesItems
            } else {
                renderablesItems.filter { it.name.contains(debouncedQuery, true) }
            }
        }
    }

    val operations = operationTargets.map { it.operation }

    fun restoreAndApplyOperationUpTo(
        targetOperation: Operation?,
        renderables: List<Renderable>,
    ) {
        // Restore position and rotation of all renderables of all models
        val modelNodeMap = modelNodes.associateBy { it.name }
        renderableStates.keys
            .groupBy { it.modelId }
            .forEach { (modelId, renderables) ->
                val modelNode = modelNodeMap[modelId.toString()] ?: return@forEach
                renderables.forEach { renderable ->
                    modelNode.renderableNodes.getOrNull(renderable.index)?.apply {
                        position = renderable.initialPosition
                        quaternion = renderable.initialQuaternion
                    }
                }
            }

        // If there is no operation selected, there is no offsets applied
        if(targetOperation == null) return

        val isNewOperation = targetOperation.orderNumber == 0

        // returns a step orderNumber of given step id
        val stepOrderNumberMap = steps.associate { it.id to it.orderNumber }

        //An operation should have first a step Id
        val targetStepOrder = stepOrderNumberMap[targetOperation.stepId] ?: return

        val sortedOpTargetsUntilCurrent = operationTargets
            .filter { stepOrderNumberMap[it.operation.stepId] != null }
            .sortedWith(
                compareBy(
                    { stepOrderNumberMap[it.operation.stepId] },
                    { it.operation.orderNumber }
                )
            )
            .takeWhile { opTarget ->
                val stepOrder =
                    stepOrderNumberMap[opTarget.operation.stepId] ?: return@takeWhile false

                if (opTarget.operation.id == 0) return@takeWhile false

                stepOrder < targetStepOrder ||
                (stepOrder == targetStepOrder && isNewOperation) ||
                (stepOrder == targetStepOrder && opTarget.operation.orderNumber < targetOperation.orderNumber)
            }

        // Apply offsets to all renderables until the current operation target
        sortedOpTargetsUntilCurrent.forEach { opTarget ->
            opTarget.renderableTargets.forEach { renderableTarget ->
                val modelNode = modelNodeMap[renderableTarget.modelId.toString()] ?: return@forEach
                modelNode.renderableNodes[renderableTarget.renderableIndex].apply{
                    position += opTarget.operation.offsetPosition
                    quaternion *= opTarget.operation.offsetQuaternion
                }
            }
        }

        // Apply offsets to all renderables of the current operation
        renderables.forEach{ renderable ->
            val modelNode = modelNodeMap[renderable.modelId.toString()] ?: return@forEach
            modelNode.renderableNodes[renderable.index].apply {
                position += targetOperation.offsetPosition
                quaternion *= targetOperation.offsetQuaternion
            }
        }
    }

    LaunchedEffect(selectedOperation, selectedRenderableItems) {
        Log.d("currentOperation", selectedOperation.toString())
        Log.d("currentOperation", selectedRenderableItems.toString())
        restoreAndApplyOperationUpTo(selectedOperation, selectedRenderableItems)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Activity's Editor") }, // todo create string
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
                    leadingIcon = { StepIcon() },
                    content = {
                        Text(
                            text = if (steps.isEmpty()) {
                                "No steps"
                            } else {
                                "Step: ${selectedStep?.orderNumber} / ${steps.size}"
                            }, // todo add string
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
                            step.orderNumber > steps.minOf { it.orderNumber }
                        } ?: true,
                        content = { ArrowBackIcon() }
                    )

                    FilledIconButton(
                        onClick = { onEvent(StepsEvent.OnSelectNextStep) },
                        modifier = Modifier.size(40.dp),
                        enabled = selectedStep != null && selectedStep.orderNumber < steps.maxOf { it.orderNumber },
                        content = { ArrowBackIcon(Modifier.rotate(180F)) }
                    )
                }

                ButtonWithIcon(
                    onClick = {
                        showSideSheet = true
                        // todo add expand current step to op
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    leadingIcon = { AnimationIcon() },
                    content = {
                        Text(
                            text = if (operations.isEmpty()) {
                                "No operations"
                            } else {
                                "Operation: ${selectedOperation?.orderNumber} / ${operations.size}"
                            }, // todo fix the count of ops (5)
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
                            key = { "${it.modelId}_${it.index}" }
                        ) { renderable ->

                            val renderableState by remember(renderable, renderableStates) {
                                derivedStateOf {
                                    renderableStates[renderable]
                                        ?: RenderableState(isVisible = true)
                                }
                            }

                            RenderableItem(
                                name = renderable.name,
                                isVisible = renderableState.isVisible,
                                isSelected = false,
                                onVisibilityChange = {
                                    onExternalEvent(
                                        StepsExternalEvent.OnToggleRenderableVisibility(
                                            renderable = renderable
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
                onEditOperation = {

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

            if(selectedStep != null && selectedOperation != null) {
                OperationBottomSheet(
                    isVisible = showBottomSheet,
                    selectedRenderables = selectedRenderableItems,
                    onUnselectItem = { onExternalEvent(StepsExternalEvent.OnUnselectRenderable(it)) },
                    onSelectionChange = {
                        onExternalEvent(StepsExternalEvent.OnToggleSelection(it))
                    },
                    onSaveClick = {
                        showBottomSheet = false
                        onExternalEvent(StepsExternalEvent.OnUnselectAllRenderables)
                        onEvent(StepsEvent.OnSaveOperation(it))
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

//@Composable
//fun EditActivityContent2(
//    models: List<Model>,
//    markers: List<Marker>,
//    currentMarker: Marker?,
//    currentBitmap: Bitmap?,
//    listsUiState: ListsUiState,
//    selectionsUiState: SelectionsUiState,
//    onNavigateBack: () -> Unit,
//    onEvent: (EditActivityEvent) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val context = LocalContext.current
//
//    val engine = rememberEngine()
//    val modelLoader = rememberModelLoader(engine)
//    val materialLoader = rememberMaterialLoader(engine)
//    val view = rememberView(engine).apply { isStencilBufferEnabled = true }
//
//    val cameraNode = rememberARCameraNode(engine)
//    val sceneNodes = rememberNodes()
//
//    var frame by remember { mutableStateOf<Frame?>(null) }
//
//    // For edition bar
//    var isEditionBarVisible by remember { mutableStateOf(true) }
//    var trackingMethod by remember { mutableStateOf(AugmentedImage.TrackingMethod.NOT_TRACKING) }
//
//    // For displaying augmented images
//    var firstTimeDetected by remember { mutableStateOf(false) }
//    var previousMarkerId by remember { mutableStateOf(0) }
//
//    // Dialogs
//    var showMarkersDialog by remember { mutableStateOf(true) }
//
//    var currentLabel by remember { mutableStateOf<LabelDialogState?>(null) }
//
//    val coroutineScope = rememberCoroutineScope()
//    val scaffoldState = rememberBottomSheetScaffoldState(
//        bottomSheetState = rememberStandardBottomSheetState(
//            initialValue = SheetValue.Hidden,
//            skipHiddenState = false
//        )
//    )
//
//    val unselectedMaterial = getModelMaterialInstance(
//        materialLoader = materialLoader,
//        modelColor = MODEL_UNSELECTED_COLOR
//    )
//
//    val validMaterialInstance = getModelMaterialInstance(
//        materialLoader = materialLoader,
//        modelColor = MODEL_VALID_COLOR
//    )
//
//    val invalidMaterialInstance = getModelMaterialInstance(
//        materialLoader = materialLoader,
//        modelColor = MODEL_INVALID_COLOR
//    )
//
//    val repositionModels = {
//        if (currentMarker != null) {
//
//            val originNode = sceneNodes
//                .filterIsInstance<PoseNode>()
//                .firstOrNull()
//
//            val markerNode = sceneNodes
//                .filterIsInstance<AugmentedImageNode>()
//                .firstOrNull()
//
//            if (originNode != null && markerNode != null) {
//
//                val modelNodes = sceneNodes.filterIsInstance<ModelNode>()
//
//                originNode.worldPosition = markerNode
//                    .getWorldPosition(currentMarker.originPosition)
//
//                originNode.worldQuaternion = markerNode
//                    .getWorldQuaternion(currentMarker.originRotation)
//
////                modelNodes.forEachApply {
////                    val model = models
////                        .find { it.id == this.name!!.toInt() }
////                        ?: return@forEachApply
////
////                    worldPosition = originNode
////                        .getWorldPosition(model.positionFromOrigin)
////
////                    worldQuaternion = originNode
////                        .getWorldQuaternion(model.rotationFromOrigin)
////                }
//
//                Toast.makeText(
//                    context,
//                    "Models repositioned to marker successfully",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    val repositionLabels = {
//        val originNode = sceneNodes
//            .filterIsInstance<PoseNode>()
//            .firstOrNull()
//
//        if (originNode != null) {
//            val labelNodes = sceneNodes
//                .filterIsInstance<ImageNode>() //todo change to imagenode
//
////            labelNodes.forEachApply {
////
////                val label = listsUiState.labels
////                    .find { it.id == this.name!!.toInt() }
////                    ?: return@forEachApply
////
////                worldPosition = originNode
////                    .getWorldPosition(label.position)
////                worldQuaternion = originNode
////                    .getWorldQuaternion(label.rotation)
////            }
//        }
//
//    }
//
//    val bottomSheetBackStack =
//        remember { mutableStateListOf<BottomSheetPage>(BottomSheetPage.StepPage) }
//
//    fun popBottomSheetPage() {
//        if (bottomSheetBackStack.size <= 2) onEvent(EditActivityEvent.OnSelectStep(null))
//        if (bottomSheetBackStack.size > 1) bottomSheetBackStack.removeLast()
//    }
//
//    LaunchedEffect(models, listsUiState.labels, firstTimeDetected) {
//
//        if (!firstTimeDetected) return@LaunchedEffect
//
//        val modelIds = models
//            .map { it.id }
//        val modelNodesIds = sceneNodes
//            .filterIsInstance<ModelNode>()
//            .map { it.name!!.toInt() }
//
//        // If there exist a ModelNode that is not in the model list, we remove it
//        modelNodesIds.forEach { modelId ->
//            if (modelIds.none { it == modelId }) {
//                sceneNodes.removeIf { it is ModelNode && it.name!!.toInt() == modelId }
//            }
//        }
//
//        // By the other hand, if there exist a model that is not in the scene, we add it
//        models.forEach { model ->
//            if (modelNodesIds.none { it == model.id }) {
//                sceneNodes.add(
//                    createModelNode(
//                        engine = engine,
//                        modelLoader = modelLoader,
//                        materialLoader = materialLoader,
//                        modelFile = model.fileUri.toFile(),
//                        modelId = model.id,
//                    )
//                )
//            }
//        }
//
//        val labelIds = listsUiState.labels
//            .map { it.id }
//
//        val labelNodesIds = sceneNodes
//            .filterIsInstance<ImageNode>() //todo change to imagenode
//            .map { it.name!!.toInt() }
//
//        labelNodesIds.forEach { labelId ->
//            if (labelIds.none { it == labelId }) { //todo change to imagenode
//                sceneNodes.removeIf { it is ImageNode && it.name!!.toInt() == labelId }
//            }
//        }
//
//        listsUiState.labels.forEach { label ->
//            if (labelNodesIds.none { it == label.id }) {
//
//                val inputStream = context.contentResolver.openInputStream(label.fileUri)
//                val bitmapFromUri = inputStream.use {
//                    BitmapFactory.decodeStream(it)
//                }
//
//                sceneNodes.add(
//                    createLabelNode(
//                        labelId = label.id,
//                        materialLoader = materialLoader,
//                        bitmap = bitmapFromUri,
//                        widthCentimeters = label.sizeCentimeters
//                    )
//                )
//            }
//        }
//
//        if (selectionsUiState.currentStep != null) {
//            repositionLabels()
//        }
//    }
//
//    LaunchedEffect(
//        bottomSheetBackStack.lastOrNull(),
//        selectionsUiState.currentAnimationsIds.size,
//        selectionsUiState.currentRenderablesIds.size
//    ) {
//
//        if (selectionsUiState.currentStep == null) return@LaunchedEffect
//
//        val modelNodes = sceneNodes.filterIsInstance<ModelNode>()
//
//
//        when (bottomSheetBackStack.lastOrNull()) {
//
//            is BottomSheetPage.RenderablesPage -> {
////                modelNodes.forEachApply {
////                    listsUiState.renderables.forEach { renderable ->
////                        if (renderable.index in selectionsUiState.currentRenderablesIds.map { it.index }) {
////                            renderableNodes[renderable.index].materialInstance =
////                                validMaterialInstance
////                        } else {
////                            renderableNodes[renderable.index].materialInstance =
////                                invalidMaterialInstance
////                        }
////                    }
////                }
//            }
//
//            is BottomSheetPage.AnimationsPage -> {
////                modelNodes.forEachApply {
////                    selectionsUiState.currentAnimationsIds.forEach { animationId ->
////                        listsUiState.animations
////                            .find { it.index == animationId.index }
////                            ?.let {
////                                playAnimation(
////                                    animationIndex = it.index,
////                                    loop = false
////                                )
////                            }
////                    }
////                }
//            }
//
//            else -> {
//                modelNodes.forEach { modelNode ->
//                    modelNode.setMaterialInstance(unselectedMaterial)
//                    for (animation in modelNode.animationCount - 1 until 0) {
//                        modelNode.stopAnimation(animation)
//                        modelNode.animator.applyAnimation(animation, 0F)
//                    }
//                }
//            }
//        }
//    }
//
//    // When we detect a marker for the first time if there exist models in the
//    // scene, we set their position and rotation from the previous calibration.
//    LaunchedEffect(firstTimeDetected) {
//
//        if (firstTimeDetected) {
//            val originNode = PoseNode(engine).apply { name = "origin" }
//            sceneNodes.add(originNode)
//            repositionModels()
//        }
//    }
//
//    BackHandler(
//        enabled = bottomSheetBackStack.size > 1 ||
//                scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
//    ) {
//        if (bottomSheetBackStack.size > 1) {
//            bottomSheetBackStack.removeLast()
//        } else {
//            coroutineScope.launch { scaffoldState.bottomSheetState.hide() }
//        }
//    }
//
////    Scaffold (
////        modifier = Modifier.fillMaxSize(),
////    ) { paddingValues ->
////        Box (
////            modifier = Modifier
////                .padding(paddingValues)
////                .fillMaxSize()
////                .clip(RoundedCornerShape(28.dp))
////                .background(Color.Black)
////        ){
////            ARScene(
////                engine = engine,
////                view = view,
////                modelLoader = modelLoader,
////                materialLoader = materialLoader,
////                childNodes = nodes,
////                planeRenderer = false, // Turns off the dots on detected flat surfaces
////                sessionConfiguration = { session: Session, config: Config ->
////                    config.setFocusMode(Config.FocusMode.AUTO)
////                    config.setLightEstimationMode(Config.LightEstimationMode.DISABLED)
////                    config.setInstantPlacementMode(Config.InstantPlacementMode.DISABLED)
////                    config.setDepthMode(
////                        when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
////                            true -> Config.DepthMode.AUTOMATIC
////                            else -> Config.DepthMode.DISABLED
////                        }
////                    )
////                },
////                onSessionUpdated = { session, frame ->
////
////                },
////                onGestureListener = rememberOnGestureListener()
////            )
////        }
////    }
//
//    BottomSheetScaffold(
//        modifier = modifier
//            .fillMaxHeight()
//            .imePadding(),
//        scaffoldState = scaffoldState,
//        sheetPeekHeight = 0.dp,
//        sheetShape = TubShapes().extraLarge,
//        sheetDragHandle = {
//            BottomSheetTopBar(
//                bottomSheetPage = bottomSheetBackStack.lastOrNull() ?: BottomSheetPage.StepPage,
//                onHideBottomSheet = { coroutineScope.launch { scaffoldState.bottomSheetState.hide() } },
//                onNavigateBack = { popBottomSheetPage() }
//            )
//        },
//        sheetContent = {
//            EditActivitySheetContent(
//                currentPage = bottomSheetBackStack.lastOrNull() ?: BottomSheetPage.StepPage,
//                currentStep = selectionsUiState.currentStep,
//                steps = listsUiState.steps,
//                labels = listsUiState.labels,
//                renderables = listsUiState.renderables,
//                animations = listsUiState.animations,
//                visibleRenderableIds = selectionsUiState.currentRenderablesIds,
//                visibleAnimationIds = selectionsUiState.currentAnimationsIds,
//                onClickRenderable = { onEvent(EditActivityEvent.OnClickRenderable(it)) },
//                onClickAnimation = { onEvent(EditActivityEvent.OnClickAnimation(it)) },
//                onCreateStep = { onEvent(EditActivityEvent.OnCreateStep(it)) },
//                onUpdateStep = { step, uri -> onEvent(EditActivityEvent.OnUpdateStep(step, uri)) },
//                onSelectStep = { onEvent(EditActivityEvent.OnSelectStep(it)) },
//                onClickPositionLabel = {
//                    currentLabel = it
//                    coroutineScope.launch { scaffoldState.bottomSheetState.hide() }
//                },
//                onChangeStepsOrder = { steps ->
//                    steps.forEach { onEvent(EditActivityEvent.OnUpdateStep(it)) }
//                },
//                onNavigateForward = { bottomSheetBackStack.add(it) }
//            )
//        },
//        sheetSwipeEnabled = false,
//        topBar = {
//            TopAppBar(
//                title = { Text("Edit Activity") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
//                            contentDescription = null
//                        )
//                    }
//                },
//                actions = {
//                    IconButton(
//                        onClick = {
//                            coroutineScope.launch {
//                                if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
//                                    scaffoldState.bottomSheetState.expand()
//                                } else {
//                                    scaffoldState.bottomSheetState.hide()
//                                }
//                            }
//                        },
//                        content = { EditIcon() }
//                    )
//                }
//            )
//        }
//    ) {
//        ARScene(
//            engine = engine,
//            modelLoader = modelLoader,
//            materialLoader = materialLoader,
//            view = view,
//            cameraNode = cameraNode,
//            sessionConfiguration = { session, config ->
//                config.setFocusMode(Config.FocusMode.AUTO)
//                config.setLightEstimationMode(Config.LightEstimationMode.DISABLED)
//                config.setInstantPlacementMode(Config.InstantPlacementMode.LOCAL_Y_UP)
//                config.setDepthMode(
//                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
//                        true -> Config.DepthMode.AUTOMATIC
//                        else -> Config.DepthMode.DISABLED
//                    }
//                )
//            },
//            childNodes = sceneNodes,
//            onSessionUpdated = { session, updatedFrame ->
//
//                frame = updatedFrame
//
//                currentBitmap?.let { bitmap ->
//                    if ((currentMarker?.id ?: return@let) != previousMarkerId) {
//
//                        sceneNodes
//                            .filterIsInstance<AugmentedImageNode>()
//                            .forEach { augmentedImageNode ->
//                                augmentedImageNode.clearChildNodes()
//                                sceneNodes.remove(augmentedImageNode)
//                            }
//
//                        previousMarkerId = currentMarker.id
//                        // Configures a single augmented image database each time
//                        // the selected marker changes. Avoiding problems like
//                        // markers being mixed up. Thank you google :)
//                        session.configure(
//                            session.config.setAugmentedImageDatabase(
//                                AugmentedImageDatabase(session).apply {
//                                    addImage(currentMarker.id.toString(), bitmap)
//                                }
//                            )
//                        )
//                    }
//                }
//
//                // Gets the last detected trackables in any previous frame.
//                val markerTrackables = updatedFrame.getUpdatedTrackables(AugmentedImage::class.java)
//
//                // The marker trackables are basically the markers QRs detected
//                // in the camera frame but as AugmentedImages in the scene.
//                markerTrackables.forEach { trackable ->
//
//                    // Checks if exists an augmentedImageNode of the actual trackable.
//                    if (sceneNodes
//                            .filterIsInstance<AugmentedImageNode>()
//                            .none { it.imageName == trackable.name }
//                    ) {
//
//                        sceneNodes
//                            .filterIsInstance<AugmentedImageNode>()
//                            .forEach { augmentedImageNode ->
//                                augmentedImageNode.clearChildNodes()
//                                sceneNodes.remove(augmentedImageNode)
//                            }
//
//                        detectMarker(
//                            engine = engine,
//                            trackable = trackable,
//                            materialLoader = materialLoader,
//                            onTrackingMethodChanged = { trackingMethod = it },
//                            onMarkerDetected = { augmentedImageNode ->
//                                sceneNodes.add(augmentedImageNode)
//                                if (currentMarker != null && !firstTimeDetected)
//                                    firstTimeDetected = true
//                            }
//                        )
//                    }
//                }
//            },
//            onGestureListener = rememberOnGestureListener(
//                onSingleTapConfirmed = { motionEvent, node ->
//
//                    if (currentLabel != null) {
//
//                        val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
//                        val hitPose = hitResults?.firstOrNull()?.hitPose
//                            ?: return@rememberOnGestureListener
//
//                        // This gets the location where the user taps
//                        val labelPosition = hitPose.position.copy(
//                            y = hitPose.position.y + 0.05F
//                        )
//
//                        val lookDirection = normalize(
//                            Float3(
//                                x = labelPosition.x - cameraNode.worldPosition.x,
//                                y = 0F,
//                                z = labelPosition.z - cameraNode.worldPosition.z
//                            )
//                        )
//
//                        // This gets the rotation of the label, vertically looking at the user
//                        val labelQuaternion = lookTowards(
//                            eye = labelPosition,
//                            forward = lookDirection,
//                            up = Float3(0F, 1F, 0F)
//                        ).toQuaternion()
//
//                        val originNode = sceneNodes
//                            .filterIsInstance<PoseNode>()
//                            .firstOrNull() ?: return@rememberOnGestureListener
//
//                        val labelPositionFromOriginLocal = originNode
//                            .getLocalPosition(labelPosition)
//
//                        val labelQuaternionFromOriginLocal = originNode
//                            .getLocalQuaternion(labelQuaternion)
//
//                        onEvent(
//                            EditActivityEvent.OnCreateLabel(
//                                label = currentLabel!!,
//                                position = labelPositionFromOriginLocal,
//                                rotation = labelQuaternionFromOriginLocal,
//                                contentUri = currentLabel!!.imageContentUri
//                            )
//                        )
//
//                        currentLabel = null
//                    }
//                }
//            ),
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp),
//            contentAlignment = Alignment.CenterEnd
//        ) {
//            AnimatedVisibility(
//                visible = isEditionBarVisible,
//                enter = slideInHorizontally(animationSpec = tween(300)) { it } +
//                        fadeIn(animationSpec = tween(200)),
//                exit = slideOutHorizontally(animationSpec = tween(300)) { it } +
//                        fadeOut(animationSpec = tween(200))
//            ) {
//                Column(
//                    modifier = Modifier
//                        .width(48.dp)
//                        .background(
//                            MaterialTheme.colorScheme.surfaceVariant,
//                            RoundedCornerShape(50)
//                        ),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    FilledIconButton(
//                        enabled = isEditionBarVisible,
//                        onClick = { showMarkersDialog = true }
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_markers),
//                            contentDescription = null
//                        )
//                    }
//                    HorizontalDivider(
//                        modifier = Modifier.padding(horizontal = 8.dp),
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    PositionButton(
//                        enabled = sceneNodes
//                            .filterIsInstance<AugmentedImageNode>()
//                            .isNotEmpty() && isEditionBarVisible,
//                        trackingMethod = trackingMethod,
//                        onTimeout = {
//                            Toast.makeText(
//                                context,
//                                "No marker found to reposition the models",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        },
//                        onSuccess = {
//                            repositionModels()
//                            repositionLabels()
//                        }
//                    )
//
//                    CalibrationButton(
//                        enabled = sceneNodes
//                            .filterIsInstance<AugmentedImageNode>()
//                            .isNotEmpty() && isEditionBarVisible,
//                        trackingMethod = trackingMethod,
//                        onTimeout = {
//                            Toast.makeText(
//                                context,
//                                "No marker found to calibrate the models",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        },
//                        onSuccess = {
//                        }
//                    )
//                }
//            }
//        }
//
//        if (showMarkersDialog) {
//            SelectedMarkerDialog(
//                currentMarker = currentMarker,
//                markers = markers,
//                onDismissRequest = { showMarkersDialog = false },
//                onMarkerClick = {
//                    onEvent(EditActivityEvent.OnSelectMarker(it))
//                    showMarkersDialog = false
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun EditActivitySheetContent(
//    currentPage: BottomSheetPage,
//    currentStep: Step?,
//    steps: List<Step>,
//    labels: List<Label>,
//    renderables: List<Renderable>,
//    animations: List<Animation>,
//    visibleRenderableIds: List<StepRenderableId>,
//    visibleAnimationIds: List<StepAnimationId>,
//    onClickRenderable: (Renderable) -> Unit,
//    onClickAnimation: (Animation) -> Unit,
//    onCreateStep: (Step) -> Unit,
//    onUpdateStep: (Step, Uri?) -> Unit,
//    onSelectStep: (Step?) -> Unit,
//    onClickPositionLabel: (LabelDialogState) -> Unit,
//    onChangeStepsOrder: (steps: List<Step>) -> Unit,
//    onNavigateForward: (BottomSheetPage) -> Unit,
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp)
//    ) {
//        when (currentPage) {
//            BottomSheetPage.StepPage -> {
//                StepPage(
//                    step = currentStep,
//                    steps = steps.sortedByDescending { it.orderNumber },
//                    onCreateStep = onCreateStep,
//                    onUpdateSteps = onChangeStepsOrder,
//                    onSelectStep = onSelectStep,
//                    onNavigateForward = onNavigateForward
//                )
//            }
//
//            is BottomSheetPage.StepDetailsPage -> {
//                StepDetailsPage(
//                    step = currentStep,
//                    onUpdateStep = onUpdateStep,
//                    onNavigateForward = onNavigateForward
//                )
//            }
//
//            is BottomSheetPage.LabelsPage -> {
//                LabelsPage(
//                    labels = labels,
//                    onAddPlaneModel = {},
//                    onClickPositionLabel = onClickPositionLabel
//                )
//            }
//
//            is BottomSheetPage.RenderablesPage -> {
//                RenderablesPage(
//                    renderables = renderables,
//                    visibleRenderableIds = visibleRenderableIds,
//                    onClickRenderable = onClickRenderable
//                )
//            }
//
//            is BottomSheetPage.AnimationsPage -> {
//                AnimationsPage(
//                    animations = animations,
//                    visibleAnimationIds = visibleAnimationIds,
//                    onClickAnimation = onClickAnimation
//                )
//            }
//        }
//    }
//}