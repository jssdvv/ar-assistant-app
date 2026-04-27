package com.jssdvv.ara.machines.domain.model

import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.OperationType
import io.github.sceneview.math.Transform

data class Operation(
    val id: Int = 0,
    val stepId: Int,
    val order: Int = 0,
    val title: String = "",

    // Animation
    val type: OperationType = OperationType.CYLINDRICAL,
    val delay: Float = 0F, // Seconds
    val duration: Float = 5F, // Seconds

    // Renderables
    val axis: Axis = Axis.Y,
    val turns: Float = 2F,
    val global: Boolean = false,

    // Relative to container coordinate system
    val offsetTransform: Transform = Transform()
)

// Cross-Ref table 1-N
data class Pivot(
    val modelId: Int,
    val xxh3: Long, // xxh3 hash from renderable's name
)

data class OperationTargets(
    val operation: Operation,
    val pivots: Set<Pivot>
)