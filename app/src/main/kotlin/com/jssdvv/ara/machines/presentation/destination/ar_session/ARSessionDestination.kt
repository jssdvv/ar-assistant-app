package com.jssdvv.ara.machines.presentation.destination.ar_session

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheel

@SuppressLint("UnrememberedMutableState")
@Composable
fun ARSessionDestination(
    onNavigateBack: () -> Unit,
    viewModel: ARSessionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ARCameraScreen(
        uiState = uiState,
        onUiEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
internal fun ARCameraScreen(
    uiState: ARSessionUiState,
    onUiEvent: (ARSessionEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    when (uiState) {
        ARSessionUiState.Loading -> {
            LoadingWheel()
        }

        is ARSessionUiState.Success -> {
//
//            val arSessionConfigured = remember { mutableStateOf(false) }
//
//            // Observa los cambios en los marker y actualiza el estado
//            if (uiState.marker.isNotEmpty() && !arSessionConfigured.value) {
//                LaunchedEffect(uiState.marker) {
//                    arSessionConfigured.value = true
//                }
//            }
//
//            if (arSessionConfigured.value){
//
//            }
//            ArCameraContent(
//                selectedChip = uiState.selectedChip,
//                markers = uiState.markers,
//                onChipClick = { chip ->
//                    onUiEvent(OnChipClick(chip))
//                },
//                onNavigateBack = onNavigateBack
//            )

        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ArCameraContent(
//    modifier: Modifier = Modifier,
//    selectedChip: Int,
//    markers: List<Pair<Marker, Bitmap>>,
//    onChipClick: (Int) -> Unit,
//    onNavigateBack: () -> Unit,
//) {
//    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
//    val isBottomSheetExpanded =
//        bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
//    val scope = rememberCoroutineScope()
//    val chipsOptions = listOf(
//        ARChipOption.REQUIREMENTS,
//        ARChipOption.INSTRUCTIONS,
//    )
//
//    BottomSheetScaffold(
//        sheetContent = {
//            Column(
//                modifier = modifier
//                    .heightIn(0.dp, 600.dp)
//                    .background(Color.Red)
//            ) {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(60) {
//                        Text("Paso número: $it")
//                    }
//                }
//            }
//        },
//        modifier = Modifier
//            .fillMaxSize(),
//        scaffoldState = bottomSheetScaffoldState,
//        sheetPeekHeight = 105.dp,
//        sheetShape = TubShapes().extraLarge,
//        sheetDragHandle = {
//            SheetDragHandle(
//                modifier = Modifier,
//                chipsOptions = chipsOptions,
//                selectedChip = selectedChip,
//                isBottomSheetExpanded = isBottomSheetExpanded,
//                onChipClick = { chip ->
//                    if (isBottomSheetExpanded && selectedChip == chip) {
//                        scope.launch {
//                            bottomSheetScaffoldState.bottomSheetState.partialExpand()
//                        }
//                    } else {
//                        scope.launch {
//                            bottomSheetScaffoldState.bottomSheetState.expand()
//                        }
//                    }
//                    onChipClick(chip)
//                }
//            )
//        },
//        sheetSwipeEnabled = true,
//        topBar = {
//            //TopBar inside the scaffold occupies that portion of background I want the camera to take
//        },
//    ) { paddingValues ->
//        val context = LocalContext.current
//
//        val engine = rememberEngine()
//        val modelLoader = rememberModelLoader(engine)
//        val materialLoader = rememberMaterialLoader(engine)
//        val environment = rememberEnvironment(engine)
//        val mainLightNode = rememberMainLightNode(engine)
//        val view = rememberView(engine).apply {
//            // Enables the stencil buffer
//            isStencilBufferEnabled = true
//        }
//        val scene = rememberScene(engine)
//        val childNodes = rememberNodes()
//
//        val filamatLocation = "material/color.filamat"
//        val filamatLocation2 = "material/toon(v68).filamat"
//        val material = materialLoader.createMaterial(filamatLocation)
//        val material2 = materialLoader.createMaterial(filamatLocation2)
//
//        val materialInstance = materialLoader.createInstance(material2).apply {
//            setParameter("baseColor", 1f, 0.25f, 0.25f)
//
//            // Enables the stencil writing
//            setStencilWrite(true)
//
//            // void glStencilOp(GLenum sfail, GLenum dpfail, GLenum dppass)
//            // Control result of passing or failing the stencil test
//            setStencilOpStencilFail(MaterialInstance.StencilOperation.KEEP)
//            setStencilOpDepthFail(MaterialInstance.StencilOperation.KEEP)
//            setStencilOpDepthStencilPass(MaterialInstance.StencilOperation.REPLACE)
//
//            // void glStencilFunc(GLenum func, GLint ref, GLuint mask)
//            // Compares how to pass or fail the stencil test
//            setStencilCompareFunction(TextureSampler.CompareFunction.ALWAYS)
//            setStencilReferenceValue(1)
//            setStencilWriteMask(0xFF)
//        }
//
//        val outlineMaterialInstance = materialLoader.createInstance(material).apply {
//            setParameter("baseColor", 0f, 0f, 0f, 1f) // Black outline
//
//            // Stencil test: only draw where stencil value is NOT 1
//            setStencilWrite(true) // Don't write, just read
//            setStencilCompareFunction(TextureSampler.CompareFunction.NOT_EQUAL)
//            setStencilReferenceValue(1)
//            setStencilWriteMask(0xFF)
//        }
//
//        val imageNodes = remember { mutableListOf<AugmentedImageNode>() }
//        val inputStream = context.assets.open("image/machine/m1/a")
//        val bitmap = BitmapFactory.decodeStream(inputStream)
//
//        Box(
//            modifier = Modifier
//                .background(Color.Green)
//                .padding(paddingValues)
//                .fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//
//
//            ARScene(
//                modifier = Modifier
//                    .fillMaxSize(),
//                engine = engine,
//                modelLoader = modelLoader,
//                materialLoader = materialLoader,
//                sessionConfiguration = { session: Session, config: Config ->
//                    val augmentedImageDatabase = AugmentedImageDatabase(session).apply {
//                        addImage("M1P1", bitmap, 0.065f)
//                    }
//                    config.setFocusMode(Config.FocusMode.AUTO)
//                    config.setAugmentedImageDatabase(augmentedImageDatabase)
//                    config.setLightEstimationMode(Config.LightEstimationMode.DISABLED)
//                    config.setInstantPlacementMode(Config.InstantPlacementMode.DISABLED)
//                    config.setDepthMode(
//                        when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
//                            true -> Config.DepthMode.AUTOMATIC
//                            else -> Config.DepthMode.DISABLED
//                        }
//                    )
//                },
//                planeRenderer = false,
//                view = view,
//                scene = scene,
//                environment = environment,
//                mainLightNode = mainLightNode,
//                childNodes = childNodes,
//                onSessionCreated = {},
//                onSessionResumed = {},
//                onSessionPaused = {},
//                onSessionFailed = {},
//                onSessionUpdated = { session: Session, frame: Frame ->
//                    /**
//                     * Gets the updated trackables in the shown frame where the type is an
//                     * augmented image to setting up the respective image node for the
//                     * current machine.
//                     */
//                    val trackables = frame.getUpdatedTrackables(AugmentedImage::class.java)
//
//                    trackables.forEach { augmentedImage ->
//                        if (
//                            imageNodes.none { node ->
//                                node.imageName == augmentedImage.name
//                            }
//                        ) {
//                            val imageNode = AugmentedImageNode(engine, augmentedImage)
//
//                            imageNode.also { augmentedNodeImage ->
//                                when (augmentedImage.name) {
//                                    "M1P1" -> {
//
//                                        val solidNode = ModelNode(
//                                            modelLoader.createModelInstance("model/model.glb"),
//                                            centerOrigin = Position(0.0f),
//                                            autoAnimate = false
//                                        ).apply {
//                                            setMaterialInstance(materialInstance)
//                                        }
//
//                                        // Outline Model (Slightly Scaled Up)
//                                        val outlineNode = ModelNode(
//                                            modelLoader.createModelInstance("model/model.glb"),
//                                            centerOrigin = Position(0.0f),
//                                            autoAnimate = false
//                                        ).apply {
//                                            scale = Scale(1.01f) // Slightly bigger
//                                            setMaterialInstance(outlineMaterialInstance)
//                                        }
////                                        childNodes.add(modelNode)
//                                        augmentedNodeImage.addChildNode(solidNode)
//                                        augmentedNodeImage.addChildNode(outlineNode)
//                                    }
//
//                                    "M1P2" -> {
//                                        augmentedNodeImage.addChildNode(
//                                            ModelNode(
//                                                modelLoader.createModelInstance("model/model.glb"),
//                                                centerOrigin = Position(0.0f),
//                                                autoAnimate = false
//                                            )
//                                        )
//                                    }
//                                }
//                                augmentedNodeImage.isVisible = true
//                            }
//                            childNodes.add(imageNode)
//                            imageNodes.add(imageNode)
//
//                        }
//                    }
//                    if(childNodes.size > 0) {
//                        childNodes.forEach { node ->
//                            node.name
//                        }
//                        val pos = childNodes[0].position
//                        Log.d(
//                            "POSITION_NODE",
//                            "pos x: ${pos}"
//                        )
//                    }
//                },
//                //onGestureListener =
//            )
//            Box(
//                modifier = Modifier
//                    .size(250.dp)
//                    .border(BorderStroke(2.dp, Color.Red))
//            ) {
//            }
//
//        }
//    }
//
//    CenterAlignedTopAppBar(
//        modifier = Modifier.background(
//            Brush.verticalGradient(
//                listOf(
//                    Color.DarkGray,
//                    Color.Transparent
//                )
//            )
//        ),
//        title = {
//            Text(
//                text = stringResource(R.string.app_name),
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//            )
//        },
//        navigationIcon = {
//            IconButton(
//                onClick = onNavigateBack
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = ""
//                )
//            }
//        },
//        colors = TopAppBarColors(
//            containerColor = Color.Transparent,
//            scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
//            navigationIconContentColor = TopAppBarDefaults.topAppBarColors().navigationIconContentColor,
//            titleContentColor = TopAppBarDefaults.topAppBarColors().titleContentColor,
//            actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor,
//        )
//    )
//}

//enum class ARChipOption(
//    @DrawableRes val selectedIconId: Int,
//    @DrawableRes val unselectedIconId: Int,
//    @StringRes val labelTextId: Int,
//    @StringRes val iconDescId: Int,
//) {
//    REQUIREMENTS(
//        selectedIconId = R.drawable.ic_requirements_filled,
//        unselectedIconId = R.drawable.ic_requirements_outlined,
//        labelTextId = R.string.drag_handle_requirements_label,
//        iconDescId = R.string.drag_handle_icon_requirements_content_desc
//    ),
//    INSTRUCTIONS(
//        selectedIconId = R.drawable.ic_instructions_filled,
//        unselectedIconId = R.drawable.ic_instructions_outlined,
//        labelTextId = R.string.drag_handle_instructions_label,
//        iconDescId = R.string.drag_handle_icon_instructions_content_desc
//    )
//}

//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//fun SheetDragHandle(
//    modifier: Modifier = Modifier,
//    chipsOptions: List<ARChipOption>,
//    selectedChip: Int = 0,
//    onChipClick: (Int) -> Unit,
//    isBottomSheetExpanded: Boolean,
//) {
//    val state = rememberLazyListState()
//    val snappingLayout = remember(state) { SnapLayoutInfoProvider(state) }
//    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)
//    val scope = rememberCoroutineScope()
//
//    LazyRow(
//        modifier = modifier.defaultMinSize(
//            minHeight = 60.dp
//        ),
//        verticalAlignment = Alignment.CenterVertically,
//        state = state,
//        flingBehavior = flingBehavior,
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(chipsOptions) { arChipOption ->
//            FilterChip(
//                modifier = Modifier.defaultMinSize(
//                    minHeight = 32.dp
//                ),
//                selected = arChipOption.ordinal == selectedChip,
//                onClick = {
//                    scope.launch {
//                        state.animateScrollToItem(arChipOption.ordinal)
//                        onChipClick(arChipOption.ordinal)
//                    }
//                },
//                label = {
//                    Text(text = stringResource(arChipOption.labelTextId))
//                },
//                leadingIcon = {
//                    Icon(
//                        painter = painterResource(
//                            if (arChipOption.ordinal == selectedChip) arChipOption.selectedIconId else arChipOption.unselectedIconId
//                        ),
//                        contentDescription = stringResource(arChipOption.iconDescId),
//                        modifier = Modifier.size(AssistChipDefaults.IconSize)
//                    )
//                },
//                trailingIcon = {
//                    if (arChipOption.ordinal == selectedChip) {
//                        Icon(
//                            imageVector = if (isBottomSheetExpanded) {
//                                Icons.Default.KeyboardArrowDown
//                            } else {
//                                Icons.Default.KeyboardArrowUp
//                            },
//                            contentDescription = "",//stringResource(R.string.categories_chip_trailing_icon_content_description),
//                            modifier = Modifier.size(AssistChipDefaults.IconSize)
//                        )
//                    }
//                },
//                shape = ButtonDefaults.shape
//            )
//        }
//    }
//}

//if (
//imageNodes.none { augmentedImageNode ->
//    augmentedImageNode.imageName == augmentedImageTrackable.name
//}
//) {
//    val imageNode = AugmentedImageNode(engine, augmentedImageTrackable)
//    imageNode.apply {
//        if (augmentedImageTrackable.name == "QR") {
//            addChildNodes(
//                setOf(
//                    ModelNode(
//                        modelLoader.createModelInstance("model/model.glb"),
//                        centerOrigin = Position(0.0f),
//                        autoAnimate = false
//                    ).apply {
//
//                        materialLoader.loadMaterialAsync("materials/unlit.mat") {
//                            if (it != null) {
//                                setMaterialInstance(
//                                    materialLoader.createInstance(
//                                        it
//                                    )
//                                )
//                            } else {
//                                setMaterialInstance(unlitMatInstance)
//                            }
//                        }
//
//                    }
//                )
//            )
//        }
//        isVisible = true
//    }
//    nodes.add(imageNode)
//    imageNodes.add(imageNode)
//}
