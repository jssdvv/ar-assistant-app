package com.jssdvv.ara.machines.presentation.destination.calibration.function

import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.components.RenderableComponent
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.GeometryNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.SphereNode

val GIZMO_CENTER_COLOR = floatArrayOf(1F, 1F, 1F)
val GIZMO_X_COLOR = floatArrayOf(1F, 0F, 0F)
val GIZMO_Y_COLOR = floatArrayOf(0F, 1F, 0F)
val GIZMO_Z_COLOR = floatArrayOf(0F, 0F, 1F)

fun createGizmoNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    centerColor: FloatArray = GIZMO_CENTER_COLOR,
    xColor: FloatArray = GIZMO_X_COLOR,
    yColor: FloatArray = GIZMO_Y_COLOR,
    zColor: FloatArray = GIZMO_Z_COLOR,
): GeometryNode {
    val centerRadius = 0.003F
    val axisRadius = 0.001F
    val axisHeight = 0.08F
    val axisPosition = 3 * axisHeight / 4

    val xAxis = CylinderNode(
        engine = engine,
        radius = axisRadius,
        height = axisHeight,
        sideCount = 6,
        materialInstance = createColorMaterialInstance(
            materialLoader = materialLoader,
            color = xColor
        )
    ).apply {
        name = "x"
        position = Position(x = axisPosition)
        quaternion = Quaternion(w = 0.707107F, z = 0.707107F)
    }

    val yAxis = CylinderNode(
        engine = engine,
        radius = axisRadius,
        height = axisHeight,
        sideCount = 6,
        materialInstance = createColorMaterialInstance(
            materialLoader = materialLoader,
            color = yColor
        )
    ).apply {
        name = "y"
        position = Position(y = axisPosition)
        quaternion = Quaternion(w = 0.707107F, y = 0.707107F)
    }

    val zAxis = CylinderNode(
        engine = engine,
        radius = axisRadius,
        height = axisHeight,
        sideCount = 6,
        materialInstance = createColorMaterialInstance(
            materialLoader = materialLoader,
            color = zColor
        )
    ).apply {
        name = "z"
        position = Position(z = axisPosition)
        quaternion = Quaternion(w = 0.707107F, x = 0.707107F)
    }

    return SphereNode(
        engine = engine,
        center = Position(),
        radius = centerRadius,
        materialInstance = createColorMaterialInstance(
            materialLoader = materialLoader,
            color = centerColor
        )
    ).apply {
        name = "gizmo"
        isTouchable = false
        isHittable = false
        isEditable = false
        addChildNode(xAxis)
        addChildNode(yAxis)
        addChildNode(zAxis)

        setGizmoPriority(this, 7)
    }
}

fun setGizmoPriority(node: Node, priority: Int) {
    if (node is RenderableComponent) node.setPriority(priority)
    node.childNodes.forEach { child ->
        setGizmoPriority(child, priority)
    }
}

// Unlit color material instance for: geometry nodes(gizmos, planes)
fun createColorMaterialInstance(
    materialLoader: MaterialLoader,
    color: FloatArray
): MaterialInstance {
    val material = materialLoader.createMaterial(COLOR_FILAMAT)
    return materialLoader.createInstance(material).apply {
        setParameter("baseColor", color[0], color[1], color[2])
        setDepthWrite(false)
        setDepthCulling(false)
    }
}