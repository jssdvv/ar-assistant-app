package com.jssdvv.ara.machines.domain.utility

import io.github.sceneview.math.Position
import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import com.google.android.filament.RenderableManager
import com.google.ar.core.AugmentedImage
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.utility.geometry.Arrow
import io.github.sceneview.ar.arcore.yDirection
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.geometries.Geometry
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.GeometryNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.PlaneNode
import io.github.sceneview.math.Size
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode.RenderableNode
import io.github.sceneview.node.ModelNode

const val INFINITE_AXIS_PREFIX = "axis_"

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

/** Sphere with three [ArrowNode] children aligned to
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
    materialInstance = materialLoader.createGizmoColorMaterialInstance(cColor)
) {
    companion object {
        const val DEFAULT_SPHERE_RADIUS = Arrow.DEFAULT_SHAFT_RADIUS * 2.5F
    }

    val xArrow = ArrowNode(
        engine = engine,
        materialInstance = materialLoader.createGizmoColorMaterialInstance(xColor)
    ).apply {
        name = Axis.X.name.lowercase()
        parent = this@GizmoNode
        quaternion = Axis.X.quaternion
        position += Axis.X.unitVector * DEFAULT_SPHERE_RADIUS
        isTouchable = false
        isHittable = false
    }

    val yArrow = ArrowNode(
        engine = engine,
        materialInstance = materialLoader.createGizmoColorMaterialInstance(yColor)
    ).apply {
        name = Axis.Y.name.lowercase()
        parent = this@GizmoNode
        quaternion = Axis.Y.quaternion
        position += Axis.Y.unitVector * DEFAULT_SPHERE_RADIUS
        isTouchable = false
        isHittable = false
    }

    val zArrow = ArrowNode(
        engine = engine,
        materialInstance = materialLoader.createGizmoColorMaterialInstance(zColor)
    ).apply {
        name = Axis.Z.name.lowercase()
        parent = this@GizmoNode
        quaternion = Axis.Z.quaternion
        position += Axis.Z.unitVector * DEFAULT_SPHERE_RADIUS
        isTouchable = false
        isHittable = false
    }

    init {
        name = "gizmo"
        setPriorityIterable(7)
        isTouchable = false
        isHittable = false
        isVisible = false
    }
}

/**
 * Scaled [CylinderNode] aligned to a single [Axis].
 * Infinite visual reference line for translation editing.
 */
class InfiniteAxisNode(
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
        const val DEFAULT_SIDE_COUNT = 4
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

/**
 * Typed [AugmentedImageNode] wrapper. Creates a [PlaneNode]
 * child if [AugmentedImage] has valid dimensions.
 */
class MarkerNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    augmentedImage: AugmentedImage,
    onTrackingMethodChanged: (AugmentedImage.TrackingMethod) -> Unit,
) : AugmentedImageNode(
    engine = engine,
    augmentedImage = augmentedImage,
    onTrackingMethodChanged = onTrackingMethodChanged,
) {
    var planeNode: PlaneNode? = if (augmentedImage.extentX > 0 && augmentedImage.extentZ > 0) {
        PlaneNode(
            engine = engine,
            size = Size(x = augmentedImage.extentX, z = augmentedImage.extentZ),
            normal = pose.yDirection,
            materialInstance = materialLoader.createMarkerColorMaterialInstance(
                PLANE_FULL_TRACKING_COLOR
            )
        ).also { addChildNode(it) }
    } else null

    init {
        name = augmentedImage.name
    }
}

/**
 * Transformation anchor positioned at the geometric
 * center of its [RenderableNode] child.
 */
class PivotNode(engine: Engine) : Node(engine) {
    var hash: Long? = null

    init {
        isTouchable = false
        isHittable = false
    }
}

/**
 * Groups a [ModelNode] with its editor overlays.
 */
class ContainerNode(engine: Engine) : Node(engine) {
    var id: Int = 0

    init {
        isTouchable = false
        isHittable = false
    }
}

/**
 * Root of the AR scene graph. Oriented
 * to match the detected AR marker.
 */
class OriginNode(engine: Engine) : Node(engine) {
    init {
        isTouchable = false
        isHittable = false
    }
}