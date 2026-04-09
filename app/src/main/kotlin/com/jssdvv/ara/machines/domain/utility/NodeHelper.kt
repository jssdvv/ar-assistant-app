package com.jssdvv.ara.machines.domain.utility

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.core.net.toFile
import com.google.android.filament.Engine
import com.google.android.filament.LightManager
import com.google.ar.core.AugmentedImage
import com.jssdvv.ara.core.domain.utility.forEachApply
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.destination.ar_session.ARSessionDestination
import com.jssdvv.ara.machines.presentation.destination.calibration.CalibrationDestination
import com.jssdvv.ara.machines.presentation.destination.steps.StepsDestination
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR
import io.github.sceneview.SceneView.Companion.DEFAULT_MAIN_LIGHT_COLOR_INTENSITY
import io.github.sceneview.components.RenderableComponent
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.managers.color
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size
import io.github.sceneview.math.centerPosition
import io.github.sceneview.math.halfExtentSize
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.LightNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.PlaneNode
import net.openhft.hashing.LongHashFunction

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

fun Node.calculateWorldPosition(
    offset: Position,
    isGlobal: Boolean = false
): Position {
    val rotatedOffsetPosition = if (isGlobal) offset else worldQuaternion * offset
    return worldPosition + rotatedOffsetPosition
}

fun Node.calculateWorldQuaternion(
    offset: Quaternion,
    isGlobal: Boolean
): Quaternion {
    val quaternion = if (isGlobal) offset * worldQuaternion else worldQuaternion * offset
    return normalize(quaternion)
}

fun Node.applyGlobalPositionOffset(offset: Position) {
    worldPosition = calculateWorldPosition(offset, true)
}

fun Node.applyObjectPositionOffset(offset: Position) {
    worldPosition = calculateWorldPosition(offset, false)
}

fun Node.applyGlobalQuaternionOffset(offsetQuaternion: Quaternion) {
    worldQuaternion = calculateWorldQuaternion(offsetQuaternion, true)
}

fun Node.applyObjectQuaternionOffset(offsetQuaternion: Quaternion) {
    worldQuaternion = calculateWorldQuaternion(offsetQuaternion, false)
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
    val modelNode = (this as? ModelNode.RenderableNode)?.findAncestor<ModelNode>() ?: return
    modelNode.findAncestor<ContainerNode>()?.let { onNodesFound(it, modelNode) }
}

fun Node.findPivotFromRenderable(
    onNodeFound: (PivotNode) -> Unit
) {
    onNodeFound((this as? ModelNode.RenderableNode)?.findAncestor<PivotNode>() ?: return)
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
fun Collection<Node>.filterBoxNodes() = filterIsInstance<CubeNode>()

val ContainerNode.modelNode: ModelNode?
    get() = childNodes.filterModelNodes().firstOrNull()

val ContainerNode.gizmoNode: GizmoNode?
    get() = childNodes.filterGizmoNodes().firstOrNull()

val ContainerNode.boxNode: CubeNode?
    get() = childNodes.filterBoxNodes().firstOrNull()

val ModelNode.pivotNodes: List<PivotNode>
    get() = childNodes.filterPivotNodes()

val PivotNode.renderableNode: ModelNode.RenderableNode?
    get() = childNodes.filterRenderableNodes().firstOrNull()

val PivotNode.gizmoNode: GizmoNode?
    get() = childNodes.filterGizmoNodes().firstOrNull()

val PivotNode.boxNode: CubeNode?
    get() = childNodes.filterBoxNodes().firstOrNull()

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
    model: Model,
    modelColor: FloatArray = MODEL_UNSELECTED_COLOR,
): ContainerNode {
    val containerNode = ContainerNode(engine).apply {
        this.modelId = model.id
    }

    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(model.glbUri.toFile()),
        autoAnimate = false
    ).apply {
        name = model.id.toString()
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

fun ModelNode.RenderableNode.setPlayingMaterialInstance(materialLoader: MaterialLoader) {
    materialInstance = materialLoader.createModelColorMaterialInstance(MODEL_PLAYING_COLOR)
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
            color = Color(1F, 1F, 1F, 0.2F),
            metallic = 0F,
            roughness = 0F,
            reflectance = 0F
        )
    ).apply {
        name = "box"
        isVisible = false
        isHittable = false
        isTouchable = false
        setPriority(6)
    }
}

fun ModelNode.generatePivotNodes(materialLoader: MaterialLoader) {
    val modelId = this.name?.toInt() ?: 0
    val containerNode = parent as? ContainerNode
    containerNode?.renderableNodes = renderableNodes
    val pivotNodes = renderableNodes.mapIndexed {_, renderableNode ->
        val center = renderableNode.axisAlignedBoundingBox.centerPosition
        val position = renderableNode.position
        val quaternion = renderableNode.quaternion
        val pivotPosition = position + quaternion * center

        val pivotNode = PivotNode(engine).apply {
            this.modelId = modelId
            this.hash = LongHashFunction.xx3().hashChars(renderableNode.name ?: "")
            this.name = renderableNode.name
            this.position = pivotPosition
            this.quaternion = quaternion
        }

        renderableNode.apply {
            this.parent = pivotNode
            this.position = -center
            this.quaternion = Quaternion()
        }

        generateBoxNode(
            engine = engine,
            size = renderableNode.axisAlignedBoundingBox.halfExtentSize * 2F,
            materialLoader = materialLoader
        ).apply {
            this.parent = pivotNode
        }

        pivotNode
    }
    containerNode?.pivotNodes = pivotNodes
    addChildNodes(pivotNodes.toSet())
}

fun Node.generateSingeAxisNode(
    axis: Axis,
    materialLoader: MaterialLoader,
    startVisible: Boolean = true
) {
    val axisNode = InfiniteAxisNode(
        engine = engine,
        materialLoader = materialLoader,
        axis = axis
    ).apply {
        isVisible = startVisible
    }
    addChildNode(axisNode)
}

fun Node.generateAllAxisNodes(
    materialLoader: MaterialLoader,
    startAllVisible: Boolean = false
) {
    Axis.entries.map {
        generateSingeAxisNode(
            axis = it,
            materialLoader = materialLoader,
            startVisible = startAllVisible
        )
    }
}

fun Node.removeAxisNodes() {
    childNodes.filterAxisNodes().forEach {
        it.safeTerminate()
        removeChildNode(it)
    }
}

fun Node.isolateAxisVisibility(
    axis: Axis?,
    materialLoader: MaterialLoader,
) {
    val axisNodes = childNodes.filterAxisNodes().also { nodes ->
        nodes.forEach { it.isVisible = false }
    }
    if(axis == null) return
    val node = axisNodes.firstOrNull { it.axis == axis }
    if (node != null) {
        node.isVisible = true
    } else {
        generateSingeAxisNode(axis, materialLoader, true)
    }
}

fun Node.generateGizmoNode(
    materialLoader: MaterialLoader,
    isGlobal: Boolean? = null,
    startVisible: Boolean = false
) {
    GizmoNode(
        engine = engine,
        materialLoader = materialLoader,
    ).apply {
        if(isGlobal == true) this.worldQuaternion = Quaternion()
        this.isVisible = startVisible
        this.parent = this@generateGizmoNode
    }
}

fun Node.removeGizmoNodes() {
    childNodes.filterGizmoNodes().forEach {
        it.safeTerminate()
        removeChildNode(it)
    }
}

fun Node.removeAllGuideNodes() {
    removeAxisNodes()
    removeGizmoNodes()
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
    val full = materialLoader.createMarkerColorMaterialInstance(fullTrackingColor)
    val last = materialLoader.createMarkerColorMaterialInstance(lastPositionColor)
    val lost = materialLoader.createMarkerColorMaterialInstance(lostTrackingColor)

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

    planeNode = markerNode.planeNode?.also { onMarkerDetected(markerNode) }
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

inline fun <reified T : Node> Node.findAncestor(): T? {
    var current: Node? = parent
    while (current != null) {
        if (current is T) return current
        current = current.parent
    }
    return null
}