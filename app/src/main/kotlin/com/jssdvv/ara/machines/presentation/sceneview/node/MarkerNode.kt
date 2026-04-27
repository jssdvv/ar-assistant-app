package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.google.ar.core.AugmentedImage
import com.jssdvv.ara.machines.presentation.sceneview.utility.PLANE_FULL_TRACKING_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.createMarkerMaterial
import io.github.sceneview.ar.arcore.yDirection
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Size
import io.github.sceneview.node.PlaneNode

/**
 * Typed [AugmentedImageNode] wrapper. Creates a [PlaneNode]
 * child if [AugmentedImage] has valid dimensions.
 */
class MarkerNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    augmentedImage: AugmentedImage,
    onTrackingMethodChanged: ((AugmentedImage.TrackingMethod) -> Unit)? = null,
) : AugmentedImageNode(
    engine = engine,
    augmentedImage = augmentedImage,
    onTrackingMethodChanged = onTrackingMethodChanged,
) {
    var planeNode: PlaneNode? = if (augmentedImage.extentX > 0 && augmentedImage.extentZ > 0) {
        PlaneNode(
            engine = engine,
            size = Size(
                x = augmentedImage.extentX,
                z = augmentedImage.extentZ
            ),
            normal = pose.yDirection,
            materialInstance = materialLoader.createMarkerMaterial(
                PLANE_FULL_TRACKING_COLOR
            )
        ).also { addChildNode(it) }
    } else null

    fun updateTrackable(trackable: AugmentedImage) {
        this@MarkerNode.trackable = trackable
        name = trackable.name
    }

    init {
        name = augmentedImage.name
        isHittable = false
        isTouchable = false
    }
}