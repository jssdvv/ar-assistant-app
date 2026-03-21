package com.jssdvv.ara.machines.presentation.destination.calibration.function

import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import java.io.File

const val MODEL_FILAMAT = "material/toon(v56).filamat"

val MODEL_SELECTED_COLOR = floatArrayOf(1F, 0.5F, 0F) // Orange
val MODEL_UNSELECTED_COLOR = floatArrayOf(0F, 0.5F, 1F) // Blue

fun createModelNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelFile: File,
    modelId: Int,
    modelColor: FloatArray = MODEL_UNSELECTED_COLOR,
): ModelNode {

    val materialInstance = getModelMaterialInstance(
        materialLoader = materialLoader,
        modelColor = modelColor
    )

    val gizmoNode = createGizmoNode(
        engine = engine,
        materialLoader = materialLoader,
    ).apply {
        isVisible = false
        isTouchable = false
        isHittable = false
        isEditable = false
        isScaleEditable = false
    }

    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(modelFile),
        autoAnimate = false
    ).apply {
        name = modelId.toString()
    }

    return modelNode.apply {
        setMaterialInstance(materialInstance)
        addChildNode(gizmoNode)
        //childNodes.elementAt(0).position = this.boundingBox.center.toFloat3()
    }
}

// lit toon color material instance for complex shapes nodes: models, renderables
fun getModelMaterialInstance(
    materialLoader: MaterialLoader,
    modelColor: FloatArray = MODEL_UNSELECTED_COLOR,
): MaterialInstance {
    val material = materialLoader.createMaterial(MODEL_FILAMAT)
    return materialLoader.createInstance(material).apply {
        setParameter("baseColor", modelColor[0], modelColor[1], modelColor[2])
    }
}