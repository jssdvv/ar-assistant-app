package com.jssdvv.ara.machines.presentation.sceneview.node

import com.google.android.filament.Engine
import com.jssdvv.ara.machines.presentation.destination.ar_session.ARSessionDestination
import com.jssdvv.ara.machines.presentation.destination.calibration.CalibrationDestination
import com.jssdvv.ara.machines.presentation.destination.steps.StepsDestination
import io.github.sceneview.math.Transform
import io.github.sceneview.node.Node

/**
 * Node hierarchy for AR scene composition.
 * Used in [ARSessionDestination] and [StepsDestination].
 *
 * ```
 * SnapshotStateList<Node>
 * ├── OriginNode (1)
 * │       └── ContainerNode (n)
 * │               └── ModelNode (1)
 * │                       └── PivotNode (n)
 * │                               ├── RenderableNode (1)
 * │                               ├── BoxNode (1: optional)
 * │                               └── GizmoNode (1: optional)
 * └── MarkerNode (1)
 * ```
 *
 * [CalibrationDestination] variant. [PivotNode]s are not present:
 *
 * ```
 * SnapshotStateList<Node>
 * ├── OriginNode (1)
 * │       └── ContainerNode (n)
 * │               ├── ModelNode (1)
 * │               ├── GizmoNode (1: optional)
 * │               └── AxisNode (3: optional)
 * └── MarkerNode (1)
 * ```
 *
 * In [CalibrationDestination], transformations are applied directly to [ContainerNode] since
 * per-renderable animation is not needed — only whole-model positioning and orientation.
 */
class OriginNode(engine: Engine) : Node(engine) {
    fun repositionToMarker(markerNode: MarkerNode?, offsetTransform: Transform?) {
        if (markerNode == null || offsetTransform == null) return
        transform = markerNode.getWorldTransform(offsetTransform)
    }

    init {
        isTouchable = false
        isHittable = false
    }
}