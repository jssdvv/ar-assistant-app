package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.sceneview.utility.createGizmoColorMaterialInstance
import com.jssdvv.ara.machines.presentation.sceneview.utility.setPriorityIterable
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Scale
import io.github.sceneview.node.CylinderNode

/**
 * Scaled [CylinderNode] aligned to a single [Axis].
 * Infinite visual reference line for translation editing.
 */
class AxisNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    val axis: Axis,
    radius: Float = DEFAULT_RADIUS,
    height: Float = DEFAULT_HEIGHT,
    scale: Float = DEFAULT_SCALE,
    sideCount: Int = DEFAULT_SIDE_COUNT
) : CylinderNode(
    engine = engine,
    radius = radius,
    height = height,
    sideCount = sideCount,
    materialInstance = materialLoader.createGizmoColorMaterialInstance(axis.color)
) {
    companion object {
        const val DEFAULT_RADIUS = 0.001F
        const val DEFAULT_HEIGHT = 1F
        const val DEFAULT_SCALE = 10F
        const val DEFAULT_SIDE_COUNT = 3
    }

    init {
        this.scale = Scale(x = 1F, y = scale, z = 1F)
        quaternion = axis.quaternion
        isVisible = false
        isTouchable = false
        isHittable = false
        setPriorityIterable(7)
    }
}