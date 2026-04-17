package com.jssdvv.ara.machines.presentation.sceneview.node

import androidx.core.net.toFile
import com.google.android.filament.Engine
import com.jssdvv.ara.core.domain.utility.forEachApply
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_UNSELECTED_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.createModelColorMaterialInstance
import com.jssdvv.ara.machines.presentation.sceneview.utility.safeTerminate
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.math.centerPosition
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import net.openhft.hashing.LongHashFunction

/**
 * Groups a [ModelNode] with its editor overlays.
 */
class ContainerNode(engine: Engine, val model: Model) : Node(engine) {
    var modelId: Int = 0

    var modelNode: ModelNode? = null
    var gizmoNode: GizmoNode? = null
    var pivotNodes: List<PivotNode> = emptyList()

    private var axisNodes: MutableMap<Axis, AxisNode> = mutableMapOf()

    fun setModelNode(modelLoader: ModelLoader, materialLoader: MaterialLoader) {
        val rawUri = model.glbUri.toString()
        val isAsset = rawUri.contains("android_asset/")

        val instance = if (isAsset) {
            val assetPath = rawUri.substringAfter("android_asset/")
            modelLoader.createModelInstance(assetPath)
        } else {
            val file = model.glbUri.toFile()
            modelLoader.createModelInstance(file)
        }

        modelNode = ModelNode(instance, false).apply{
            this.name = this@ContainerNode.model.id.toString()
            this.isHittable = false
            this.isTouchable = false
            this.position = -(this.quaternion * this.boundingBox.centerPosition)
            this.parent = this@ContainerNode
            this.setMaterialInstance(materialLoader.createModelColorMaterialInstance(
                MODEL_UNSELECTED_COLOR
            ))
            this.renderableNodes.forEach { renderable ->
                renderable.isHittable = true
                renderable.isTouchable = true
                renderable.updateCollisionShape()
            }
        }
    }

    fun generatePivotNodes() {
        val modelNode = this@ContainerNode.modelNode ?: return
        if(pivotNodes.isNotEmpty()) return
        pivotNodes = modelNode.renderableNodes.map { renderableNode ->
            val center = renderableNode.axisAlignedBoundingBox.centerPosition
            val position = renderableNode.position + renderableNode.quaternion * center
            val quaternion =  renderableNode.quaternion
            val pivot = PivotNode(engine).apply {
                this.name = renderableNode.name
                this.modelId = this@ContainerNode.modelId
                this.hash = LongHashFunction.xx3().hashChars(renderableNode.name ?: "")
                this.renderableNode = renderableNode
                this.parent = this@ContainerNode
                this.position = position
                this.quaternion = quaternion
                this.initialPosition = position
                this.initialQuaternion = quaternion
            }

            renderableNode.apply {
                this.parent = pivot
                // Don't touch this future me
                this.position = -center
                this.quaternion = Quaternion()
            }

            pivot
        }
    }

    fun restorePivotsTransform() {
        pivotNodes.forEachApply {
            position = initialPosition
            quaternion = initialQuaternion
        }
    }

    private fun generateGizmoNode(materialLoader: MaterialLoader) {
        if (gizmoNode != null) return
        gizmoNode = GizmoNode(engine, materialLoader).also { addChildNode(it) }
    }

    fun generateAxesNodes(materialLoader: MaterialLoader) {
        Axis.entries.forEach { axis ->
            if (axisNodes.containsKey(axis)) return@forEach
            axisNodes[axis] = AxisNode(engine, materialLoader, axis).also { addChildNode(it) }
        }
    }

    fun setGizmoVisibility(visible: Boolean, materialLoader: MaterialLoader) {
        if(visible) generateGizmoNode(materialLoader)
        gizmoNode?.isVisible = visible
    }

    fun setAxisVisibility(axis: Axis?, materialLoader: MaterialLoader) {
        axisNodes.values.forEachApply { isVisible = false }
        if (axis == null) return
        val axisNode = axisNodes[axis]
        if (axisNode != null) {
            axisNode.isVisible = true
        } else {
            axisNodes[axis] = AxisNode(engine, materialLoader, axis).also {
                addChildNode(it)
                it.isVisible = true
            }
        }
    }

    fun restorePositionDefaults() { position = Position() }
    fun restoreQuaternionDefaults() { quaternion = Quaternion() }

    fun terminate() {
        modelNode?.safeTerminate()
        gizmoNode?.safeTerminate()
        pivotNodes.forEach(Node::safeTerminate)
        axisNodes.values.forEach(Node::safeTerminate)
        modelNode = null
        gizmoNode = null
        pivotNodes = emptyList()
        axisNodes.clear()
        safeTerminate()
    }

    init {
        modelId = model.id
        transform = model.offsetTransform
        isTouchable = false
        isHittable = false
    }
}