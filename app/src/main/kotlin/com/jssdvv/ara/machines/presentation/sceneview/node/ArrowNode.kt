package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import com.google.android.filament.RenderableManager
import com.jssdvv.ara.machines.presentation.sceneview.geometry.Arrow
import io.github.sceneview.geometries.Geometry
import io.github.sceneview.node.GeometryNode

class ArrowNode private constructor(
    engine: Engine,
    override val geometry: Geometry,
    materialInstances: List<MaterialInstance?>,
    primitivesOffsets: List<IntRange> = geometry.primitivesOffsets,
    builderApply: RenderableManager.Builder.() -> Unit = {}
) : GeometryNode(
    engine = engine,
    geometry = geometry,
    materialInstances = materialInstances,
    primitivesOffsets = primitivesOffsets,
    builderApply = builderApply
) {
    init {
        isTouchable = false
        isHittable = false
    }

    constructor(
        engine: Engine,
        geometry: Geometry,
        materialInstance: MaterialInstance? = null,
        builderApply: RenderableManager.Builder.() -> Unit = {}
    ) : this(
        engine = engine,
        geometry = geometry,
        materialInstances = listOf(materialInstance),
        primitivesOffsets = listOf(0..geometry.primitivesOffsets.last().last),
        builderApply = builderApply
    )

    constructor(
        engine: Engine,
        shaftRadius: Float = Arrow.DEFAULT_SHAFT_RADIUS,
        shaftHeight: Float = Arrow.DEFAULT_SHAFT_HEIGHT,
        sideCount: Int = Arrow.DEFAULT_SIDE_COUNT,
        headRadius: Float = Arrow.DEFAULT_HEAD_RADIUS,
        headHeight: Float = Arrow.DEFAULT_HEAD_HEIGHT,
        materialInstance: MaterialInstance? = null,
        builderApply: RenderableManager.Builder.() -> Unit = {}
    ) : this(
        engine = engine,
        geometry = Arrow.Builder()
            .shaftRadius(shaftRadius)
            .shaftHeight(shaftHeight)
            .sideCount(sideCount)
            .headRadius(headRadius)
            .headHeight(headHeight)
            .build(engine),
        materialInstances = listOf(materialInstance),
        builderApply = builderApply
    )

    constructor(
        engine: Engine,
        shaftRadius: Float = Arrow.DEFAULT_SHAFT_RADIUS,
        shaftHeight: Float = Arrow.DEFAULT_SHAFT_HEIGHT,
        sideCount: Int = Arrow.DEFAULT_SIDE_COUNT,
        headRadius: Float = Arrow.DEFAULT_HEAD_RADIUS,
        headHeight: Float = Arrow.DEFAULT_HEAD_HEIGHT,
        materialInstances: List<MaterialInstance?>,
        builderApply: RenderableManager.Builder.() -> Unit = {}
    ) : this(
        engine = engine,
        geometry = Arrow.Builder()
            .shaftRadius(shaftRadius)
            .shaftHeight(shaftHeight)
            .sideCount(sideCount)
            .headRadius(headRadius)
            .headHeight(headHeight)
            .build(engine),
        materialInstances = materialInstances,
        builderApply = builderApply
    )
}