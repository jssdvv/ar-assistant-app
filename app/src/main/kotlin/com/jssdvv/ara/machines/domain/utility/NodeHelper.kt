package com.jssdvv.ara.machines.domain.utility

import com.jssdvv.ara.core.domain.utility.forEachApply
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.components.RenderableComponent
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node

fun repositionOrigin(
    originNode: Node,
    markerNode: AugmentedImageNode,
    originOffsetPosition: Position,
    originOffsetQuaternion: Quaternion,
) {
    originNode.apply{
        position = markerNode.getWorldPosition(originOffsetPosition)
        quaternion = markerNode.getWorldQuaternion(originOffsetQuaternion)
    }
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
    if(this is RenderableComponent) setPriority(priority)
    childNodes.forEachApply { setPriorityIterable(priority) }
}

fun Node.setGizmoVisibility(isVisible: Boolean) {
    childNodes.find { it.name == "gizmo" }?.isVisible = isVisible
}

fun Node.safeTerminate() {
    childNodes.toList().forEach(Node::safeTerminate)
    clearChildNodes()
    destroy()
}