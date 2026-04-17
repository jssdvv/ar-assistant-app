package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.jssdvv.ara.machines.presentation.sceneview.utility.safeTerminate
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.math.halfExtentSize
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node

/**
 * Transformation anchor positioned at the geometric
 * center of its [ModelNode.RenderableNode] child.
 */
class PivotNode(engine: Engine) : Node(engine) {
    var modelId: Int = 0
    var hash: Long = 0

    var initialPosition: Position = Position()
    var initialQuaternion: Quaternion = Quaternion()

    var boxNode: BoxNode? = null
    var gizmoNode: GizmoNode? = null
    var renderableNode: ModelNode.RenderableNode? = null

    private fun generateBoxNode(materialLoader: MaterialLoader) {
        if (boxNode != null) return
        val size = renderableNode?.axisAlignedBoundingBox?.halfExtentSize?.times(2F) ?: return
        boxNode = BoxNode(engine, size, materialLoader).also { addChildNode(it) }
    }

    private fun generateGizmoNode(materialLoader: MaterialLoader) {
        if (gizmoNode != null) return
        gizmoNode = GizmoNode(engine, materialLoader).also { addChildNode(it) }
    }

    fun setSelectionVisuals(
        visible: Boolean,
        materialLoader: MaterialLoader,
        global: Boolean = false
    ) {
        if (visible) {
            generateBoxNode(materialLoader)
            generateGizmoNode(materialLoader)

            boxNode?.isVisible = true
            gizmoNode?.apply {
                this.isVisible = true
                this.worldQuaternion = if (global) Quaternion() else this@PivotNode.worldQuaternion
            }
        } else {
            boxNode?.isVisible = false
            gizmoNode?.isVisible = false
        }
    }

    fun updateGizmoOrientation(isGlobal: Boolean) {
        gizmoNode?.apply {
            if (isGlobal) {
                worldQuaternion = Quaternion()
            } else {
                quaternion = Quaternion()
            }
        }
    }

    fun terminate() {
        boxNode?.safeTerminate()
        gizmoNode?.safeTerminate()
        boxNode = null
        gizmoNode = null
        renderableNode = null
        terminate()
    }

    init {
        isTouchable = false
        isHittable = false
    }
}