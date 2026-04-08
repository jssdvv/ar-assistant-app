package com.jssdvv.ara.machines.domain.utility

import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.Speed
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.slerp
import io.github.sceneview.math.Position
import io.github.sceneview.math.lerp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Renderable identifier holder
 */
data class RenderableInfo(
    val modelId: Int,
    val xxh3: Long
)

/**
 * Renderable base temporal state holder interface
 */
interface RestorableState {
    val index: Int
    val initialPosition: Position
    val initialQuaternion: Quaternion
}

/**
 * Restore position and rotation of all pivots of all containers passed
 */
fun restorePivotsTransforms(
    renderableInfoStates: Map<RenderableInfo, RestorableState>,
    containersMap: Map<Int, ContainerNode> // Map<Model.id, ContainerNode>
) {
    renderableInfoStates.forEach { (info, state) ->
        val containerNode = containersMap[info.modelId] ?: return@forEach
        containerNode.pivotNodes.getOrNull(state.index)?.apply {
            position = state.initialPosition
            quaternion = state.initialQuaternion
        }
    }
}

/**
 * Apply offsets of all operations passed depending on global boolean Operation.isGlobal
 */
fun applyOperationOffsets(
    operationTargets: List<OperationTargets>,
    renderableInfoStates: Map<RenderableInfo, RestorableState>,
    containersMap: Map<Int, ContainerNode> // Map<Model.id, ContainerNode>
) {
    operationTargets.forEach { operationTarget ->
        operationTarget.targets.forEach { renderableTarget ->
            val containerNode = containersMap[renderableTarget.modelId] ?: return@forEach
            val info = RenderableInfo(renderableTarget.modelId, renderableTarget.xxh3)
            val state = renderableInfoStates[info] ?: return@forEach

            containerNode.pivotNodes.getOrNull(state.index)?.apply {
                if (operationTarget.operation.isGlobal) {
                    applyGlobalPositionOffset(operationTarget.operation.offsetPosition)
                    applyGlobalQuaternionOffset(operationTarget.operation.offsetRotation)
                } else {
                    applyObjectPositionOffset(operationTarget.operation.offsetPosition)
                    applyObjectQuaternionOffset(operationTarget.operation.offsetRotation)
                }
            }
        }
    }
}

fun applyOperationsOffsetsBeforeTo(
    currentOperation: Operation?,
    steps: List<Step>,
    operationsTargets: List<OperationTargets>,
    renderableInfoStates: Map<RenderableInfo, RestorableState>,
    containersMap: Map<Int, ContainerNode> // Map<Model.id, ContainerNode>
) {
    restorePivotsTransforms(renderableInfoStates, containersMap)

    val isNewOperation = (currentOperation?.id ?: return) == 0
    val stepsOrdersMap = steps.associate { it.id to it.order } // Map<Step.id, Step.order>
    val currentStepOrder = stepsOrdersMap[currentOperation.stepId] ?: return

    val operationTargetsFiltered = operationsTargets
        .sortedWith(
            compareBy(
                { stepsOrdersMap[it.operation.stepId] },
                { it.operation.order }
            )
        )
        .takeWhile { operationTarget ->
            val stepOrder =
                stepsOrdersMap[operationTarget.operation.stepId] ?: return@takeWhile false

            if (operationTarget.operation.id == 0) return@takeWhile false

            stepOrder < currentStepOrder ||
                    (stepOrder == currentStepOrder && isNewOperation) ||
                    (stepOrder == currentStepOrder && operationTarget.operation.order < currentOperation.order)
        }

    applyOperationOffsets(
        operationTargets = operationTargetsFiltered,
        renderableInfoStates = renderableInfoStates,
        containersMap = containersMap
    )
}

suspend fun launchOperationAnimation(
    currentOperation: Operation,
    operationsTargets: List<OperationTargets>,
    renderableInfoStates: Map<RenderableInfo, RestorableState>,
    selectedRenderableInfoStates: Map<RenderableInfo, RestorableState>,
    containersMap: Map<Int, ContainerNode>, // Map<Model.id, ContainerNode>
    isEditionEnabled: Boolean,
    currentSpeed: Speed,
    isPlaying: Boolean,
    isLoopingEnabled: Boolean,
) {
    val nodesToAnimate: List<PivotNode> = if (isEditionEnabled) {
        // From current selections
        selectedRenderableInfoStates.mapNotNull { (info, state) ->
            containersMap[info.modelId]?.pivotNodes?.getOrNull(state.index)
        }
    } else {
        // From DB
        operationsTargets
            .find { it.operation.id == currentOperation.id }
            ?.targets
            ?.mapNotNull { target ->
                val info = RenderableInfo(target.modelId, target.xxh3)
                val state = renderableInfoStates[info] ?: return@mapNotNull null
                containersMap[target.modelId]?.pivotNodes?.getOrNull(state.index)
            } ?: emptyList()
    }

    coroutineScope {
        nodesToAnimate.map { pivotNode ->
            launch {
                pivotNode.animate(
                    operation = currentOperation,
                    initialPosition = pivotNode.worldPosition,
                    initialQuaternion = pivotNode.worldQuaternion,
                    speed = currentSpeed,
                    isPlaying = isPlaying,
                    isLoopingEnabled = isLoopingEnabled,
                )
            }
        }.joinAll()
    }
}

suspend fun PivotNode.animate(
    operation: Operation,
    initialPosition: Position,
    initialQuaternion: Quaternion,
    speed: Speed,
    isPlaying: Boolean,
    isLoopingEnabled: Boolean,
) {
    val finalPosition = calculateWorldPosition(operation.offsetPosition, operation.isGlobal)
    val finalQuaternion = calculateWorldQuaternion(operation.offsetRotation, operation.isGlobal)

    val isScrew = operation.type == OperationType.SCREW
    val totalDegrees = operation.turns * 360F

    val baseDelayMs = operation.delay * 1000F
    val baseDurationMs = operation.duration * 1000F
    val postDelayMs = 800L

    var lastNanos = 0L

    var delayTimeMs = 0F
    var animationTimeMs = 0F
    var postDelayTimeMs = 0F
    var reverseTimeMs = 0F

    suspend fun updateMs(onUpdate: (Float) -> Unit) {
        withFrameNanos { currentNanos ->
            if (isPlaying) {
                val deltaNanos = if (lastNanos == 0L) 0 else currentNanos - lastNanos
                lastNanos = currentNanos
                val deltaMs = deltaNanos * speed.denominator / 1_000_000F
                onUpdate(deltaMs)
            } else {
                lastNanos = 0L
            }
        }
    }

    suspend fun pauseUntilResumed(onResume: () -> Unit) {
        snapshotFlow { isPlaying }.first { it }
        onResume()
    }

    fun animationQuaternion(ratio: Float): Quaternion = if (isScrew) {
        initialQuaternion * unidirectionalRotation(operation.axis, ratio * totalDegrees)
    } else {
        slerp(initialQuaternion, finalQuaternion, ratio)
    }

    while (true) {
        // Delay
        while (delayTimeMs < baseDelayMs) {
            worldPosition = initialPosition
            worldQuaternion = initialQuaternion
            updateMs { delayDeltaMs -> delayTimeMs += delayDeltaMs }
            if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
            yield()
        }

        // Animation
        while (animationTimeMs < baseDurationMs) {
            updateMs { animationDeltaMs -> animationTimeMs += animationDeltaMs }
            val ratio = (animationTimeMs / baseDurationMs).coerceIn(0F..1F)
            worldPosition = lerp(initialPosition, finalPosition, ratio)
            worldQuaternion = animationQuaternion(ratio)

            if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
            yield()
        }

        worldPosition = finalPosition
        worldQuaternion = finalQuaternion

        // Post Animation
        if (isLoopingEnabled) {

            // Post Delay
            while (postDelayTimeMs < postDelayMs) {
                updateMs { postDelayDeltaMs -> postDelayTimeMs += postDelayDeltaMs }
                if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
                yield()
            }

            // Reverse Animation
            while (reverseTimeMs < baseDurationMs) {
                updateMs { reverseDeltaMs -> reverseTimeMs += reverseDeltaMs }
                val ratio = 1 - (reverseTimeMs / baseDurationMs).coerceIn(0F..1F)
                worldPosition = lerp(initialPosition, finalPosition, ratio)
                worldQuaternion = animationQuaternion(ratio)

                if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
                yield()
            }

            // Reset
            delayTimeMs = 0F
            animationTimeMs = 0F
            postDelayTimeMs = 0F
            reverseTimeMs = 0F
            lastNanos = 0L
        } else {
            return
        }
    }
}