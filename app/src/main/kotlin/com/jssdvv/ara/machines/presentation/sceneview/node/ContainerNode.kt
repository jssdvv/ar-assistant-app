package com.jssdvv.ara.machines.presentation.sceneview.node

import androidx.core.net.toFile
import com.google.android.filament.Engine
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.sceneview.utility.AxisNodesMap
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_UNSELECTED_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.createModelMaterial
import com.jssdvv.ara.machines.presentation.sceneview.utility.objectOffset
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.math.centerPosition
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import net.openhft.hashing.LongHashFunction

/**
 * Groups a [ModelNode] with its editor overlays.
 */
class ContainerNode(engine: Engine, val model: Model) : Node(engine) {

    companion object {
        private const val ASSET_PREFIX = "android_asset/"
    }

    var modelId: Int = 0

    var modelNode: ModelNode? = null
    var gizmoNode: GizmoNode? = null
    var pivotNodes: List<PivotNode> = emptyList()

    private var axisNodes: AxisNodesMap = mutableMapOf()

    fun setModelNode(modelLoader: ModelLoader, materialLoader: MaterialLoader) {
        val rawUri = model.glbUri.toString()

        val instance = if (rawUri.contains(ASSET_PREFIX)) {
            modelLoader.createModelInstance(rawUri.substringAfter(ASSET_PREFIX))
        } else {
            modelLoader.createModelInstance(model.glbUri.toFile())
        }

        modelNode = ModelNode(instance, false).apply {
            this.name = this@ContainerNode.model.id.toString()
            this.isHittable = false
            this.isTouchable = false
            this.position = -(this.quaternion * this.boundingBox.centerPosition)
            this.parent = this@ContainerNode
            this.setMaterialInstance(materialLoader.createModelMaterial(MODEL_UNSELECTED_COLOR))
            this.renderableNodes.forEach { renderable ->
                renderable.isHittable = true
                renderable.isTouchable = true
                renderable.updateCollisionShape()
            }
        }
    }

    fun generatePivotNodes() {
        val modelNode = this@ContainerNode.modelNode ?: return
        if (pivotNodes.isNotEmpty()) return

        pivotNodes = modelNode.renderableNodes.map { renderable ->

            val center = renderable.axisAlignedBoundingBox.centerPosition
            val pivotTransform = getLocalTransform(renderable).objectOffset(center)

            PivotNode(engine).apply {
                modelId = this@ContainerNode.modelId
                parent = this@ContainerNode
                name = renderable.name
                hash = LongHashFunction.xx3().hashChars(renderable.name ?: "")
                transform = pivotTransform
                initialTransform = pivotTransform
                renderableNode = renderable.apply { transform = Transform(-center) }
            }
        }
    }

    private fun generateGizmoNode(materialLoader: MaterialLoader) {
        if (gizmoNode != null) return
        gizmoNode = GizmoNode(engine, materialLoader).also { addChildNode(it) }
    }

    fun setGizmoVisibility(visible: Boolean, materialLoader: MaterialLoader) {
        if (visible) generateGizmoNode(materialLoader)
        gizmoNode?.isVisible = visible
    }

    fun setAxisVisibility(axis: Axis?, materialLoader: MaterialLoader) {
        axisNodes.values.onEach { it.isVisible = false }
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

    fun restorePosition() {
        position = Position()
    }

    fun restoreQuaternion() {
        quaternion = Quaternion()
    }

    init {
        modelId = model.id
        transform = model.offsetTransform
        isTouchable = false
    }
}