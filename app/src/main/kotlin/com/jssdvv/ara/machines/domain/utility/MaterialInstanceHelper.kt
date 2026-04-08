package com.jssdvv.ara.machines.domain.utility

import io.github.sceneview.loaders.MaterialLoader

const val MODEL_FILAMAT = "material/toon.filamat"
const val COLOR_FILAMAT = "material/color.filamat"

val MODEL_SELECTED_COLOR = floatArrayOf(1.00F, 0.60F, 0.40F)
val MODEL_PLAYING_COLOR = floatArrayOf(0.80F, 0.20F, 0.20F)
val MODEL_UNSELECTED_COLOR = floatArrayOf(0.20F, 0.68F, 1.00F)

val DISABLED_COLOR = floatArrayOf(0.50F, 0.50F, 0.50F)
val GIZMO_C_COLOR = floatArrayOf(1.00F, 0.60F, 0.20F)
val GIZMO_X_COLOR = floatArrayOf(1.00F, 0.00F, 0.00F)
val GIZMO_Y_COLOR = floatArrayOf(0.00F, 1.00F, 0.00F)
val GIZMO_Z_COLOR = floatArrayOf(0.00F, 0.00F, 1.00F)

val PLANE_LOST_TRACKING_COLOR = floatArrayOf(1.00F, 0.20F, 0.20F)
val PLANE_FULL_TRACKING_COLOR = floatArrayOf(0.00F, 0.90F, 0.45F)
val PLANE_LAST_POSITION_COLOR = floatArrayOf(1.00F, 0.80F, 0.00F)

/**
 * Material instance for complex shapes like models, renderables.
 */
fun MaterialLoader.createModelColorMaterialInstance(
    color: FloatArray = DISABLED_COLOR,
    alpha: Float = 1F
) = createInstance(createMaterial(MODEL_FILAMAT)).apply {
    setParameter("baseColor", color[0], color[1], color[2], alpha)
}

/**
 * Material instance for visual guides geometry like gizmos or axis.
 */
fun MaterialLoader.createGizmoColorMaterialInstance(color: FloatArray = DISABLED_COLOR) =
    createInstance(createMaterial(COLOR_FILAMAT)).apply {
        setParameter("baseColor", color[0], color[1], color[2])
        setDepthCulling(false)
        setDepthWrite(false)
    }

/**
 * Material instance for visual markers.
 */
fun MaterialLoader.createMarkerColorMaterialInstance(color: FloatArray = DISABLED_COLOR) =
    createInstance(createMaterial(COLOR_FILAMAT)).apply {
        setParameter("baseColor", color[0], color[1], color[2])
    }