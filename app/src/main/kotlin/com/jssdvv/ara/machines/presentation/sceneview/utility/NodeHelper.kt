package com.jssdvv.ara.machines.presentation.sceneview.utility

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.filament.Engine
import com.google.ar.core.AugmentedImage
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.sceneview.node.AxisNode
import com.jssdvv.ara.machines.presentation.sceneview.node.ContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.MarkerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.PivotNode
import io.github.sceneview.components.RenderableComponent
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Transform
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.PlaneNode
import io.github.sceneview.safeDestroyEntity
import io.github.sceneview.safeDestroyTransformable

typealias ModelId = Int
typealias OperationId = Int
typealias ContainerNodesMap = MutableMap<ModelId, ContainerNode>
typealias PivotNodesMap = MutableMap<Pivot, PivotNode>
typealias AxisNodesMap = MutableMap<Axis, AxisNode>
typealias PivotOffsetsMap = Map<Pivot, Transform>
typealias PivotsTransformsMap = Map<OperationId, PivotOffsetsMap>


val SnapshotStateList<Node>.markerNode: MarkerNode? get() = getOrNull(1) as? MarkerNode

fun SnapshotStateList<Node>.safeTerminate(nodes: Collection<Node> = this.toList()) {
    nodes.safeTerminate()
    removeAll(nodes)
}

fun Collection<Node>.safeTerminate() = forEach(Node::safeTerminate)

fun Node.safeTerminate() {
    childNodes.safeTerminate()
    runCatching { parent = null }
    engine.safeDestroyTransformable(entity)
    engine.safeDestroyEntity(entity)
}

fun Node.setPriorityIterable(priority: Int) {
    (this as? RenderableComponent)?.setPriority(priority)
    childNodes.forEach { it.setPriorityIterable(priority) }
}

fun ModelNode.setSelectedMaterial(materialLoader: MaterialLoader) {
    setMaterialInstance(materialLoader.createModelMaterial(MODEL_SELECTED_COLOR))
}

fun ModelNode.setUnselectedMaterial(materialLoader: MaterialLoader) {
    setMaterialInstance(materialLoader.createModelMaterial(MODEL_UNSELECTED_COLOR))
}

fun pivotsOffsets(
    pivotNodesMap: PivotNodesMap,
    transformedTargets: List<OperationTargets>
): PivotOffsetsMap = buildMap {
    transformedTargets.forEach { (operation, pivots) ->
        pivots.forEach { pivot ->
            val node = pivotNodesMap[pivot] ?: return@forEach
            val previous = get(pivot) ?: node.initialTransform
            put(pivot, previous.offset(operation.offsetTransform, operation.global))
        }
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
    val full = materialLoader.createMarkerMaterial(fullTrackingColor)
    val last = materialLoader.createMarkerMaterial(lastPositionColor)
    val lost = materialLoader.createMarkerMaterial(lostTrackingColor)

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

inline fun <reified T : Node> Node.findAncestorOrNull(): T? {
    var current: Node? = parent
    while (current != null) {
        if (current is T) return current
        current = current.parent
    }
    return null
}