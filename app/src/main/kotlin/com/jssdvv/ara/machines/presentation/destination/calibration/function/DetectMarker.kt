package com.jssdvv.ara.machines.presentation.destination.calibration.function

import com.google.android.filament.Engine
import com.google.ar.core.AugmentedImage
import io.github.sceneview.ar.arcore.yDirection
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Size
import io.github.sceneview.node.PlaneNode

const val COLOR_FILAMAT = "material/color.filamat"

val PLANE_NOT_TRACKING_COLOR = floatArrayOf(1F, 0F, 0F) // Red
val PLANE_FULL_TRACKING_COLOR = floatArrayOf(0F, 1F, 0F) // Green
val PLANE_LAST_KNOWN_POSITION_COLOR = floatArrayOf(1F, 1F, 0F) // Yellow

fun detectMarker(
    engine: Engine,
    materialLoader: MaterialLoader,
    trackable: AugmentedImage,
    onTrackingMethodChanged: (AugmentedImage.TrackingMethod) -> Unit,
    fullTrackingColor: FloatArray = PLANE_FULL_TRACKING_COLOR,
    lastKnownPositionColor: FloatArray = PLANE_LAST_KNOWN_POSITION_COLOR,
    notTrackingColor: FloatArray = PLANE_NOT_TRACKING_COLOR,
    onMarkerDetected: (AugmentedImageNode) -> Unit,
) {
    val material = materialLoader.createMaterial(COLOR_FILAMAT)

    val fullTrackingMaterialInstance = materialLoader.createInstance(material).apply {
        setParameter(
            "baseColor",
            fullTrackingColor[0],
            fullTrackingColor[1],
            fullTrackingColor[2]
        )
    }

    val lastKnownPositionMaterialInstance = materialLoader.createInstance(material).apply {
        setParameter(
            "baseColor",
            lastKnownPositionColor[0],
            lastKnownPositionColor[1],
            lastKnownPositionColor[2]
        )
    }

    val notTrackingMaterialInstance = materialLoader.createInstance(material).apply {
        setParameter(
            "baseColor",
            notTrackingColor[0],
            notTrackingColor[1],
            notTrackingColor[2]
        )
    }

    var planeNode: PlaneNode? = null

    val augmentedImageNode = AugmentedImageNode(
        engine = engine,
        augmentedImage = trackable,
        onTrackingMethodChanged = { trackingMethod ->

            planeNode?.materialInstance = when (trackingMethod) {
                AugmentedImage.TrackingMethod.FULL_TRACKING -> fullTrackingMaterialInstance
                AugmentedImage.TrackingMethod.LAST_KNOWN_POSE -> lastKnownPositionMaterialInstance
                AugmentedImage.TrackingMethod.NOT_TRACKING -> notTrackingMaterialInstance
            }

            onTrackingMethodChanged(trackingMethod)
        }
    ).apply { name = trackable.name }

    // Create a plane only if the marker has valid dimensions
    if (trackable.extentX > 0 && trackable.extentZ > 0) {
        planeNode = PlaneNode(
            engine = engine,
            size = Size(x = trackable.extentX, z = trackable.extentZ),
            normal = augmentedImageNode.pose.yDirection,
            materialInstance = fullTrackingMaterialInstance
        )

        augmentedImageNode.addChildNode(planeNode)
        onMarkerDetected(augmentedImageNode)
    }
}