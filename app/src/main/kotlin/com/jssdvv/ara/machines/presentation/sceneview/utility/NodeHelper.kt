package com.jssdvv.ara.machines.presentation.sceneview.utility

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.filament.Engine
import com.google.ar.core.AugmentedImage
import com.jssdvv.ara.core.domain.utility.forEachApply
import com.jssdvv.ara.machines.presentation.sceneview.node.MarkerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.OriginNode
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.components.RenderableComponent
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.PlaneNode

fun OriginNode.offset(
    markerNode: MarkerNode,
    offsetPosition: Position,
    direction: Direction = Direction()
) {
    position = markerNode.worldPosition + markerNode.worldQuaternion * offsetPosition
    this.lookTowards(lookDirection = direction)
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

fun SnapshotStateList<Node>.safeTerminate(nodes: Collection<Node>) {
    nodes.forEach(Node::safeTerminate)
    removeAll(nodes)
}

fun Collection<Node>.filterMarkerNodes() = filterIsInstance<MarkerNode>()

val SnapshotStateList<Node>.markerNode: MarkerNode?
    get() = getOrNull(1) as? MarkerNode


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

inline fun <reified T : Node> Node.findAncestorOrNull(): T? {
    var current: Node? = parent
    while (current != null) {
        if (current is T) return current
        current = current.parent
    }
    return null
}