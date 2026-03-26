package com.jssdvv.ara.machines.domain.model

import com.jssdvv.ara.machines.domain.type.OperationType
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

data class Operation(
    val id: Int = 0,
    val stepId: Int,
    val orderNumber: Int = 0,
    val title: String,
    val type: OperationType,
    val duration: Float = 5F, // Seconds
    val delay: Float = 0F, // Seconds
    val offsetPosition: Position = Position(),
    val offsetQuaternion: Quaternion = Quaternion(),
    val screwPitch: Float = 2.0F, // Millimeters
    val axis: Float3 = Float3(0f, 1f, 0f),
    val pivot: Float3 = Float3(0f, 0f, 0f),
)

// Cross-Ref table 1-N
data class RenderableTarget(
    val operationId: Int,
    val modelId: Int,
    val renderableIndex: Int,
    val renderableName: String = ""
)

data class OperationTargets(
    val operation: Operation,
    val renderableTargets: List<RenderableTarget>
)