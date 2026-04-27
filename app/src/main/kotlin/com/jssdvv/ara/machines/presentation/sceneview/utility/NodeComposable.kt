package com.jssdvv.ara.machines.presentation.sceneview.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import com.google.android.filament.Engine
import com.google.android.filament.LightManager
import com.google.ar.core.AugmentedImage
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.presentation.sceneview.node.ContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.MarkerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.OriginNode
import io.github.sceneview.SceneView
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.managers.color
import io.github.sceneview.node.CameraNode
import io.github.sceneview.node.LightNode
import io.github.sceneview.node.Node

@Composable
fun rememberNodes(
    creator: MutableList<Node>.() -> Unit = {}
) = remember {
    buildList(creator).toMutableStateList()
}.also { nodes ->
    DisposableEffect(nodes) {
        onDispose { nodes.safeTerminate() }
    }
}

@Composable
inline fun <reified T : Node> rememberNode(
    key: Any? = Unit,
    crossinline creator: () -> T
) = remember(key, creator)

@Composable
inline fun <reified T : Node> rememberDisposableNode(
    key: Any? = Unit,
    crossinline creator: () -> T
) = remember(key, creator).also { node ->
    DisposableEffect(node) {
        onDispose { node.safeTerminate() }
    }
}

@Composable
fun rememberOriginNode(
    engine: Engine,
    apply: OriginNode.() -> Unit = {}
) = rememberNode { OriginNode(engine).apply(apply) }

@Composable
fun rememberContainerNode(
    model: Model,
    engine: Engine,
    apply: ContainerNode.() -> Unit = {}
) = rememberNode(model.id) { ContainerNode(engine, model).apply(apply) }

@Composable
fun rememberMarkerNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    augmentedImage: AugmentedImage,
    onTrackingMethodChanged: ((AugmentedImage.TrackingMethod) -> Unit)? = null,
    apply: MarkerNode.() -> Unit
) = rememberNode {
    MarkerNode(
        engine = engine,
        materialLoader = materialLoader,
        augmentedImage = augmentedImage,
        onTrackingMethodChanged = onTrackingMethodChanged
    ).apply(apply)
}

@Composable
fun rememberCameraNode(
    engine: Engine,
    apply: CameraNode.() -> Unit = {},
) = rememberDisposableNode { SceneView.DefaultCameraNode(engine).apply(apply) }

@Composable
fun rememberLightNode(
    engine: Engine,
    apply: LightNode.() -> Unit = {}
) = rememberDisposableNode {
    LightNode(
        engine = engine,
        type = LightManager.Type.DIRECTIONAL,
        apply = {
            color(SceneView.DEFAULT_MAIN_LIGHT_COLOR)
            intensity(SceneView.DEFAULT_MAIN_LIGHT_COLOR_INTENSITY)
            direction(0F, -1F, -1F)
            castShadows(true)
        }
    ).apply(apply)
}