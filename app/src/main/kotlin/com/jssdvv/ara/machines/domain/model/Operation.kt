package com.jssdvv.ara.machines.domain.model

import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.OperationType
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

data class Operation(
    val id: Int = 0,
    val stepId: Int,
    val order: Int = 0,
    val title: String,

    // Animation
    val type: OperationType,
    val delay: Float = 0F, // Seconds
    val duration: Float = 8F, // Seconds

    // Renderables
    val axis: Axis = Axis.X,
    val pivot: Float3 = Float3(), // todo, delete this
    val alpha: Float = 1F, // Restarts Every Step
    val pitch: Float = 2F, // Millis
    val turns: Float = 2F,
    val isGlobal: Boolean = false,
    val offsetPosition: Position = Position(),
    val offsetRotation: Quaternion = Quaternion()
)

// Cross-Ref table 1-N
data class RenderableTarget(
    val operationId: Int,
    val modelId: Int,
    val xxh3: Long,
    val name: String
)

data class OperationTargets(
    val operation: Operation,
    val targets: List<RenderableTarget>
)