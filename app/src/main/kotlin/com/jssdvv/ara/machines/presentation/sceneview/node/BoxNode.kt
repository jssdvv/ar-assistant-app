package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.jssdvv.ara.machines.presentation.sceneview.utility.createBoxMaterialInstance
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size
import io.github.sceneview.node.CubeNode

class BoxNode(
    engine: Engine,
    size: Size,
    materialLoader: MaterialLoader,
    center: Position = Position()
): CubeNode(
    engine = engine,
    size = size + Size(0.005F),
    center = center,
    materialInstance = materialLoader.createBoxMaterialInstance()
) {
    init {
        isHittable = false
        isTouchable = false
        setPriority(6)
    }
}