package com.jssdvv.ara.machines.domain.utility

import com.jssdvv.ara.machines.presentation.destination.ar_session.ARSessionDestination
import com.jssdvv.ara.machines.presentation.destination.steps.StepsDestination
import com.jssdvv.ara.machines.presentation.destination.calibration.CalibrationDestination
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.google.android.filament.Engine
import com.google.android.filament.LightManager
import com.google.ar.core.AugmentedImage
import com.jssdvv.ara.core.domain.utility.forEachApply
import com.jssdvv.ara.machines.domain.type.Axis
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR_INTENSITY
import io.github.sceneview.ar.arcore.yDirection
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.components.RenderableComponent
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.managers.color
import io.github.sceneview.math.Position
import io.github.sceneview.math.centerPosition
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.PlaneNode
import io.github.sceneview.math.Size
import io.github.sceneview.math.halfExtentSize
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.LightNode
import java.io.File

/**
 * Node hierarchy for AR scene composition.
 * Used in [ARSessionDestination] and [StepsDestination].
 * See [CalibrationDestination] variant at the bottom.
 *
 * ```
 * SnapshotStateList<Node>
 * ├── OriginNode (1)
 * │       └── ContainerNode (N)
 * │               ├── ModelNode (1)
 * │               │       └── PivotNode (N)
 * │               │               ├── RenderableNode (1: repositioned)
 * │               │               ├── CubeNode (1)
 * │               │               ├── GizmoNode (1: optional)
 * │               │               └── InfiniteAxisNode (3: optional)
 * │               ├── CubeNode (1)
 * │               └── GizmoNode (1: optional)
 * └── MarkerNode (N)
 * ```
 * ---
 *
 * [CalibrationDestination] variant. [PivotNode]s are not present:
 *
 * ```
 * SnapshotStateList<Node>
 * ├── OriginNode (1)
 * │       └── ContainerNode (N)
 * │               ├── ModelNode (1)
 * │               ├── CubeNode (1)
 * │               └── GizmoNode (1: optional)
 * └── MarkerNode (N)
 * ```
 *
 * In [CalibrationDestination], transformations are applied directly to [ContainerNode] since
 * per-renderable animation is not needed — only whole-model positioning and orientation.
 */
fun OriginNode.offset(
    markerNode: MarkerNode,
    offsetPosition: Position,
    offsetQuaternion: Quaternion
) {
    position = markerNode.getWorldPosition(offsetPosition)
    quaternion = markerNode.getWorldQuaternion(offsetQuaternion)
}

fun Node.applyGlobalPositionOffset(offsetPosition: Position) {
    worldPosition += offsetPosition
}

fun Node.applyObjectPositionOffset(offsetPosition: Position) {
    worldPosition += worldQuaternion * offsetPosition
}

fun Node.applyGlobalQuaternionOffset(offsetQuaternion: Quaternion) {
    worldQuaternion = normalize(offsetQuaternion * worldQuaternion)
}

fun Node.applyObjectQuaternionOffset(offsetQuaternion: Quaternion) {
    worldQuaternion = normalize(worldQuaternion * offsetQuaternion)
}

fun Node.setPriorityIterable(priority: Int) {
    if (this is RenderableComponent) setPriority(priority)
    childNodes.forEachApply { setPriorityIterable(priority) }
}

fun Node.setGizmoVisibility(visible: Boolean) {
    childNodes.filterGizmoNodes().forEachApply { isVisible = visible }
}

fun Node.findModelInContainerFromRenderable(
    onNodesFound: (ContainerNode, ModelNode) -> Unit
) {
    if (this !is ModelNode.RenderableNode) return
    val modelNode = findParent<ModelNode>() ?: return
    modelNode.findParent<ContainerNode>()?.let { onNodesFound(it, modelNode) }
}

fun Node.safeTerminate() {
    childNodes.forEach(Node::safeTerminate)
    clearChildNodes()
    destroy()
}

fun SnapshotStateList<Node>.safeTerminate(nodes: Collection<Node>) {
    nodes.forEach(Node::safeTerminate)
    removeAll(nodes)
}

fun SnapshotStateList<Node>.safeTerminate(node: Node) {
    node.safeTerminate()
    remove(node)
}

fun Collection<Node>.filterModelNodes() = filterIsInstance<ModelNode>()
fun Collection<Node>.filterMarkerNodes() = filterIsInstance<MarkerNode>()
fun Collection<Node>.filterGizmoNodes() = filterIsInstance<GizmoNode>()
fun Collection<Node>.filterPivotNodes() = filterIsInstance<PivotNode>()
fun Collection<Node>.filterRenderableNodes() = filterIsInstance<ModelNode.RenderableNode>()
fun Collection<Node>.filterAxisNodes() = filterIsInstance<InfiniteAxisNode>()
fun Collection<Node>.filterContainerNodes() = filterIsInstance<ContainerNode>()
fun Collection<Node>.filterBoxNode() = filterIsInstance<CubeNode>()

val ContainerNode.modelNode: ModelNode?
    get() = childNodes.filterModelNodes().firstOrNull()

val ContainerNode.gizmoNode: GizmoNode?
    get() = childNodes.filterGizmoNodes().firstOrNull()

val ContainerNode.boxNode: CubeNode?
    get() = childNodes.filterBoxNode().firstOrNull()

val ContainerNode.pivotNodes: List<PivotNode>
    get() = modelNode?.pivotNodes ?: emptyList()

val ContainerNode.renderableNodes: List<ModelNode.RenderableNode>
    get() = modelNode?.renderableNodes ?: emptyList()

val ModelNode.pivotNodes: List<PivotNode>
    get() = childNodes.filterPivotNodes()

val ModelNode.renderablePivotedNodes: List<ModelNode.RenderableNode>
    get() = pivotNodes.mapNotNull { it.renderableNode }

val PivotNode.renderableNode: ModelNode.RenderableNode?
    get() = childNodes.filterRenderableNodes().firstOrNull()

val PivotNode.gizmoNode: GizmoNode?
    get() = childNodes.filterGizmoNodes().firstOrNull()

val PivotNode.infiniteAxisNodes: List<InfiniteAxisNode>
    get() = childNodes.filterIsInstance<InfiniteAxisNode>()

val SnapshotStateList<Node>.originNode: OriginNode?
    get() = filterIsInstance<OriginNode>().firstOrNull()

val SnapshotStateList<Node>.containerNodes: List<ContainerNode>
    get() = originNode?.childNodes?.filterContainerNodes() ?: emptyList()

val SnapshotStateList<Node>.markerNodes: List<MarkerNode>
    get() = filterMarkerNodes()

fun createContainerNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelFile: File,
    modelId: Int,
    modelColor: FloatArray = MODEL_UNSELECTED_COLOR,
): ContainerNode {
    val containerNode = ContainerNode(engine).apply {
        name = modelId.toString()
        generateGizmoNode(engine, materialLoader)
    }

    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(modelFile),
        autoAnimate = false
    ).apply {
        name = modelId.toString()
        isHittable = false
        isTouchable = false
        position = -(quaternion * boundingBox.centerPosition)
        parent = containerNode
        setMaterialInstance(materialLoader.createModelColorMaterialInstance(modelColor))
        renderableNodes.forEach { renderable ->
            renderable.isHittable = true
            renderable.isTouchable = true
            renderable.updateCollisionShape()
        }
    }

    generateBoxNode(
        engine = engine,
        size = modelNode.boundingBox.halfExtentSize * 2F,
        materialLoader = materialLoader
    ).apply { parent = containerNode }

    return containerNode
}

fun ModelNode.setSelectedMaterialInstance(materialLoader: MaterialLoader) {
    setMaterialInstance(materialLoader.createModelColorMaterialInstance(MODEL_SELECTED_COLOR))
}

fun ModelNode.setUnselectedMaterialInstance(materialLoader: MaterialLoader) {
    setMaterialInstance(materialLoader.createModelColorMaterialInstance(MODEL_UNSELECTED_COLOR))
}

fun ModelNode.RenderableNode.setSelectedMaterialInstance(materialLoader: MaterialLoader) {
    materialInstance = materialLoader.createModelColorMaterialInstance(MODEL_SELECTED_COLOR)
}

fun ModelNode.RenderableNode.setUnselectedMaterialInstance(materialLoader: MaterialLoader) {
    materialInstance = materialLoader.createModelColorMaterialInstance(MODEL_UNSELECTED_COLOR)
}

fun generateBoxNode(
    engine: Engine,
    size: Size,
    materialLoader: MaterialLoader
): CubeNode {
    return CubeNode(
        engine = engine,
        size = size + Size(0.005F),
        center = Position(),
        materialInstance = materialLoader.createColorInstance(
            color = Color(1F, 1F, 1F, 0.3F),
            metallic = 0.1F,
            reflectance = 1F
        )
    ).apply {
        name = "box"
        isVisible = false
        isHittable = false
        isTouchable = false
    }
}

fun ModelNode.generatePivotNodes(materialLoader: MaterialLoader) {
    renderableNodes.forEach { renderableNode ->
        val center = renderableNode.axisAlignedBoundingBox.centerPosition
        val position = renderableNode.position
        val quaternion = renderableNode.quaternion
        val pivotPosition = position + quaternion * center

        val pivotNode = PivotNode(engine).apply {
            name = renderableNode.name
            this.position = pivotPosition
            this.quaternion = quaternion
            generateGizmoNode(engine, materialLoader) // todo remove, this is just testing
        }

        renderableNode.apply {
            parent = pivotNode
            this.position = - center
            this.quaternion = Quaternion()
        }

        generateBoxNode(
            engine = engine,
            size = renderableNode.axisAlignedBoundingBox.halfExtentSize * 2F,
            materialLoader = materialLoader
        ).apply {
            parent = pivotNode
        }

        addChildNode(pivotNode)
    }
}

fun Node.generateAxisNodes(
    engine: Engine,
    materialLoader: MaterialLoader,
) {
    Axis.entries.forEach {
        InfiniteAxisNode(
            engine = engine,
            materialLoader = materialLoader,
            axis = it
        ).apply { parent = this@generateAxisNodes }
    }
}

fun Node.removeAxisNodes() {
    childNodes.filterAxisNodes().forEach {
        it.safeTerminate()
        removeChildNode(it)
    }
}

fun Node.isolateAxisVisibility(
    axis: Axis,
    engine: Engine,
    materialLoader: MaterialLoader,
) {
    val axisNodes = childNodes.filterAxisNodes()
    if (axisNodes.isNotEmpty()) {
        val axisName = "${INFINITE_AXIS_PREFIX}${axis.name.lowercase()}"
        axisNodes.forEach { it.isVisible = it.name == axisName }
    } else {
        generateAxisNodes(engine, materialLoader)
        isolateAxisVisibility(axis, engine, materialLoader)
    }
}

fun Node.generateGizmoNode(
    engine: Engine,
    materialLoader: MaterialLoader,
) {
    GizmoNode(
        engine = engine,
        materialLoader = materialLoader
    ).apply { parent = this@generateGizmoNode }
}

fun Node.removeGizmoNodes() {
    childNodes.filterGizmoNodes().forEach {
        it.safeTerminate()
        removeChildNode(it)
    }
}

fun AugmentedImage.detectMarkerNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    fullTrackingColor: FloatArray = PLANE_FULL_TRACKING_COLOR,
    lastPositionColor: FloatArray = PLANE_LAST_POSITION_COLOR,
    lostTrackingColor: FloatArray = PLANE_LOST_TRACKING_COLOR,
    onTrackingMethodChanged: (AugmentedImage.TrackingMethod) -> Unit = {},
    onMarkerDetected: (MarkerNode) -> Unit
) {
    var planeNode: PlaneNode? = null

    val (full, last, lost) = with(materialLoader) {
        Triple(
            createMarkerColorMaterialInstance(fullTrackingColor),
            createMarkerColorMaterialInstance(lastPositionColor),
            createMarkerColorMaterialInstance(lostTrackingColor)
        )
    }

    val markerNode = MarkerNode(
        engine = engine,
        materialLoader = materialLoader,
        augmentedImage = this,
        onTrackingMethodChanged = { trackingMethod ->
            planeNode?.materialInstance = when (trackingMethod) {
                AugmentedImage.TrackingMethod.FULL_TRACKING -> full
                AugmentedImage.TrackingMethod.LAST_KNOWN_POSE -> last
                AugmentedImage.TrackingMethod.NOT_TRACKING -> lost
            }
            onTrackingMethodChanged(trackingMethod)
        }
    )

    if (markerNode.planeNode != null) {
        planeNode = markerNode.planeNode
        onMarkerDetected(markerNode)
    }
}

fun detectMarker(
    engine: Engine,
    materialLoader: MaterialLoader,
    trackable: AugmentedImage,
    fullTrackingColor: FloatArray = PLANE_FULL_TRACKING_COLOR,
    lastPositionColor: FloatArray = PLANE_LAST_POSITION_COLOR,
    lostTrackingColor: FloatArray = PLANE_LOST_TRACKING_COLOR,
    onTrackingMethodChanged: (AugmentedImage.TrackingMethod) -> Unit = {},
    onMarkerDetected: (AugmentedImageNode) -> Unit,
) {
    var planeNode: PlaneNode? = null
    val augmentedImageNode = AugmentedImageNode(
        engine = engine,
        augmentedImage = trackable,
        onTrackingMethodChanged = { trackingMethod ->
            planeNode?.materialInstance = when (trackingMethod) {
                AugmentedImage.TrackingMethod.FULL_TRACKING ->
                    materialLoader.createGizmoColorMaterialInstance(fullTrackingColor)

                AugmentedImage.TrackingMethod.LAST_KNOWN_POSE ->
                    materialLoader.createGizmoColorMaterialInstance(lastPositionColor)

                AugmentedImage.TrackingMethod.NOT_TRACKING ->
                    materialLoader.createGizmoColorMaterialInstance(lostTrackingColor)
            }

            onTrackingMethodChanged(trackingMethod)
        }
    ).apply { name = trackable.name }

    // Create a plane only if the marker has valid dimensions
    if (trackable.extentX > 0 && trackable.extentZ > 0) {
        planeNode = PlaneNode(
            engine = engine,
            size = Size(x = trackable.extentX, z = trackable.extentZ),
            normal = augmentedImageNode.pose.yDirection,
            materialInstance = materialLoader.createMarkerColorMaterialInstance(fullTrackingColor)
        ).apply {
            //setPriority(7)
            isVisible = true
        }

        augmentedImageNode.addChildNode(planeNode)
        onMarkerDetected(augmentedImageNode)
    }
}

fun createMainLightNode(engine: Engine): LightNode {
    return LightNode(
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

inline fun <reified T : Node> Node.findParent(): T? {
    var current: Node? = parent
    while (current != null) {
        if (current is T) return current
        current = current.parent
    }
    return null
}