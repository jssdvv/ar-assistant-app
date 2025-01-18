package com.jssdvv.ara.machinery.presentation.camera

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.ar.core.AugmentedImage
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.theme.TubShapes
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun ARCameraScreen(
    onNavigateBack: () -> Unit,
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    var selectedChip by remember { mutableStateOf(0) }
    val chipsOptions = listOf(
        ARChipOption.REQUIREMENTS,
        ARChipOption.INSTRUCTIONS,
    )

    BottomSheetScaffold(
        sheetContent = {
            Column(
                modifier = Modifier
                    .heightIn(0.dp,600.dp)
                    .background(Color.Red)
            ){
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(60) {
                        Text("Paso número: $it")
                    }
                }
            }

        },
        modifier = Modifier
            .fillMaxSize(),
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 105.dp,
        sheetShape = TubShapes().extraLarge,
        sheetDragHandle = {
            SheetDragHandle(
                modifier = Modifier,
                chipsOptions = chipsOptions,
                selectedChip = selectedChip,
                onClick = {

                }
            )
        },
        sheetSwipeEnabled = true,
        topBar = {
            //TopBar inside the scaffold occupies that portion of background I want the camera to take
        },
    ) { paddingValues ->
        val context = LocalContext.current

        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val environment = rememberEnvironment(engine)
        val mainLightNode = rememberMainLightNode(engine)
        val view = rememberView(engine)
        val scene = rememberScene(engine)
        val childNodes = rememberNodes()

        val inputStream = context.assets.open("images/qr_example.jpg")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val imageNode = remember { mutableStateOf<AugmentedImageNode?>(null) }

        Box(
            modifier = Modifier
                .background(Color.Green)
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            ARScene(
                modifier = Modifier
                    .fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                sessionConfiguration = { session: Session, config: Config ->
                    val augmentedImageDatabase = AugmentedImageDatabase(session).apply {
                        addImage("QR", bitmap, 0.06f)
                    }
                    config.setFocusMode(Config.FocusMode.AUTO)
                    config.setAugmentedImageDatabase(augmentedImageDatabase)
                    config.setLightEstimationMode(Config.LightEstimationMode.DISABLED)
                    config.setInstantPlacementMode(Config.InstantPlacementMode.DISABLED)
                    config.setDepthMode(
                        when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            true -> Config.DepthMode.AUTOMATIC
                            else -> Config.DepthMode.DISABLED
                        }
                    )
                },
                planeRenderer = false,
                view = view,
                scene = scene,
                environment = environment,
                mainLightNode = mainLightNode,
                childNodes = childNodes,
                onSessionCreated = {},
                onSessionResumed = {},
                onSessionPaused = {},
                onSessionFailed = {},
                onSessionUpdated = { session: Session, frame: Frame ->
                    /**
                     * Gets the updated trackables in the shown frame where the type is an
                     * augmented image to setting up the respective image node for the
                     * current machine.
                     */
                    val augmentedImagesTrackables =
                        frame.getUpdatedTrackables(AugmentedImage::class.java)

                    augmentedImagesTrackables.forEach { augmentedImage ->

                    }
                },
                //onGestureListener =
            )
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .border(BorderStroke(2.dp,Color.Red))
            ){

            }
        }
    }

    CenterAlignedTopAppBar(
        modifier = Modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color.DarkGray,
                    Color.Transparent
                )
            )
        ),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = ""
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
            navigationIconContentColor = TopAppBarDefaults.topAppBarColors().navigationIconContentColor,
            titleContentColor = TopAppBarDefaults.topAppBarColors().titleContentColor,
            actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor,
        )
    )
}

enum class ARChipOption(
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unselectedIconId: Int,
    @StringRes val labelTextId: Int,
    @StringRes val iconDescId: Int,
) {
    REQUIREMENTS(
        selectedIconId = R.drawable.requirements_filled,
        unselectedIconId = R.drawable.requirements_outlined,
        labelTextId = R.string.requirements_label_text,
        iconDescId = R.string.requirements_icon_desc
    ),
    INSTRUCTIONS(
    selectedIconId = R.drawable.instructions_filled,
    unselectedIconId = R.drawable.instructions_outlined,
    labelTextId = R.string.instructions_label_text,
    iconDescId = R.string.instructions_icon_desc
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SheetDragHandle(
    modifier: Modifier = Modifier,
    chipsOptions: List<ARChipOption>,
    selectedChip: Int = 0,
    onClick: () -> Unit
) {
    val state = rememberLazyListState()
    val snappingLayout = remember(state) { SnapLayoutInfoProvider(state) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)
    val scope = rememberCoroutineScope()

    LazyRow(
        modifier = Modifier.defaultMinSize(
            minHeight = 60.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        state = state,
        flingBehavior = flingBehavior,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(chipsOptions){arChipOption ->
            FilterChip(
                modifier = Modifier.defaultMinSize(
                    minHeight = 32.dp
                ),
                selected = arChipOption.ordinal == selectedChip,
                onClick = {
                    scope.launch {
                        state.animateScrollToItem(arChipOption.ordinal)
                    }
                },
                label = {
                    Text(text = stringResource(arChipOption.labelTextId))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            if (arChipOption.ordinal == selectedChip) arChipOption.selectedIconId else arChipOption.unselectedIconId
                        ),
                        contentDescription = stringResource(arChipOption.iconDescId),
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (arChipOption.ordinal == selectedChip) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = stringResource(R.string.categories_chip_trailing_icon_content_description),
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                },
                shape = ButtonDefaults.shape
            )
        }
    }
}

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
//                        modelLoader.createModelInstance("models/cabeza.glb"),
//                        centerOrigin = Position(0.0f),
//                        autoAnimate = false
//                    ).apply {
//
//                        materialLoader.loadMaterialAsync("materials/unlit.filamat") {
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
