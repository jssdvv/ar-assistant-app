package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.sceneview.geometry.Arrow
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_C_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_X_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_Y_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_Z_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.createGizmoColorMaterialInstance
import com.jssdvv.ara.machines.presentation.sceneview.utility.setPriorityIterable
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.SphereNode

/**
 * Sphere with three [ArrowNode] children aligned to
 * [Axis.X], [Axis.Y], [Axis.Z]. Hidden by default.
 */
class GizmoNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    cColor: FloatArray = GIZMO_C_COLOR,
    xColor: FloatArray = GIZMO_X_COLOR,
    yColor: FloatArray = GIZMO_Y_COLOR,
    zColor: FloatArray = GIZMO_Z_COLOR,
) : SphereNode(
    engine = engine,
    center = io.github.sceneview.math.Position(),
    radius = DEFAULT_SPHERE_RADIUS,
    materialInstance = materialLoader.createGizmoColorMaterialInstance(cColor)
) {
    companion object {
        const val DEFAULT_SPHERE_RADIUS = Arrow.Companion.DEFAULT_SHAFT_RADIUS * 2.5F
    }

    val xArrowNode = ArrowNode(
        engine = engine,
        materialInstance = materialLoader.createGizmoColorMaterialInstance(xColor)
    ).apply {
        this.parent = this@GizmoNode
        this.quaternion = Axis.X.quaternion
        this.position += Axis.X.unitVector * DEFAULT_SPHERE_RADIUS
    }

    val yArrowNode = ArrowNode(
        engine = engine,
        materialInstance = materialLoader.createGizmoColorMaterialInstance(yColor)
    ).apply {
        this.parent = this@GizmoNode
        this.quaternion = Axis.Y.quaternion
        this.position += Axis.Y.unitVector * DEFAULT_SPHERE_RADIUS
    }

    val zArrowNode = ArrowNode(
        engine = engine,
        materialInstance = materialLoader.createGizmoColorMaterialInstance(zColor)
    ).apply {
        this.parent = this@GizmoNode
        this.quaternion = Axis.Z.quaternion
        this.position += Axis.Z.unitVector * DEFAULT_SPHERE_RADIUS
    }

    init {
        isHittable = false
        isTouchable = false
        setPriorityIterable(7)
    }
}