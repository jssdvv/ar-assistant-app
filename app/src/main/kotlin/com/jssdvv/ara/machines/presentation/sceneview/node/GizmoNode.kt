package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.presentation.sceneview.geometry.Arrow
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_C_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_X_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_Y_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.GIZMO_Z_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.createGizmoMaterial
import com.jssdvv.ara.machines.presentation.sceneview.utility.setPriorityIterable
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
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
    center = Position(),
    radius = DEFAULT_SPHERE_RADIUS,
    materialInstance = materialLoader.createGizmoMaterial(cColor)
) {
    companion object {
        const val DEFAULT_SPHERE_RADIUS = Arrow.DEFAULT_SHAFT_RADIUS * 2.5F
    }

    init {
        isHittable = false
        isTouchable = false
        setPriorityIterable(7)
        childNodes = setOf(
            ArrowNode(
                engine = engine,
                materialInstance = materialLoader.createGizmoMaterial(xColor)
            ).apply {
                this.quaternion = Quaternion(w = 0.707107F, z = -0.707107F)
                this.position += Axis.X.unitVector * DEFAULT_SPHERE_RADIUS
            },
            ArrowNode(
                engine = engine,
                materialInstance = materialLoader.createGizmoMaterial(yColor)
            ).apply {
                this.quaternion = Quaternion(w = 0.707107F, y = 0.707107F)
                this.position += Axis.Y.unitVector * DEFAULT_SPHERE_RADIUS
            },
            ArrowNode(
                engine = engine,
                materialInstance = materialLoader.createGizmoMaterial(zColor)
            ).apply {
                this.parent = this@GizmoNode
                this.quaternion = Quaternion(w = 0.707107F, x = 0.707107F)
                this.position += Axis.Z.unitVector * DEFAULT_SPHERE_RADIUS
            }
        )
    }
}