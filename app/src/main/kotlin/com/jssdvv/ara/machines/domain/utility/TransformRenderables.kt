package com.jssdvv.ara.machines.domain.utility

import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import com.jssdvv.ara.core.domain.utility.forEachApply
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.Speed
import com.jssdvv.ara.machines.presentation.destination.steps.OperationId
import com.jssdvv.ara.machines.presentation.destination.steps.PivotsOffsetMap
import com.jssdvv.ara.machines.presentation.destination.steps.functions.unidirectionalRotation
import com.jssdvv.ara.machines.presentation.sceneview.node.ContainerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.PivotNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.applyGlobalPositionOffset
import com.jssdvv.ara.machines.presentation.sceneview.utility.applyGlobalQuaternionOffset
import com.jssdvv.ara.machines.presentation.sceneview.utility.applyObjectPositionOffset
import com.jssdvv.ara.machines.presentation.sceneview.utility.applyObjectQuaternionOffset
import com.jssdvv.ara.machines.presentation.sceneview.utility.calculateWorldPosition
import com.jssdvv.ara.machines.presentation.sceneview.utility.calculateWorldQuaternion
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
data class PivotInfo(
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
    renderableInfoStates: Map<PivotInfo, RestorableState>,
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
 * Restore position and rotation of all pivots using the reference lookup
 */
fun restorePivotsTransforms2(
    pivotsMap: Map<PivotInfo, PivotNode>
) {
    pivotsMap.values.forEachApply {
        position = initialPosition
        quaternion = initialQuaternion
    }
}

fun restorePivotsTransforms3(
    operationsTargets: List<OperationTargets>,
    offsetsSnapshot: Map<OperationId, PivotsOffsetMap>,
    pivotsMap: Map<PivotInfo, PivotNode>
) {
    val lastOpTargets = operationsTargets.last()
    val operationId = lastOpTargets.operation.id
    val offsets = offsetsSnapshot[operationId]?.keys ?: return

    offsets.mapNotNull(pivotsMap::get).forEachApply {
        position = initialPosition
        quaternion = initialQuaternion
    }
}

fun applyOffsetBeforeTo(
    operationsTargets: List<OperationTargets>,
    currentOperation: Operation?,
    offsetsSnapshot: Map<OperationId, PivotsOffsetMap>,
    pivotsMap: Map<PivotInfo, PivotNode>
) {
    val operations = operationsTargets.map { it.operation }
    val index = operations.indexOfFirst { it.id == currentOperation?.id }
    val previousOperation = operations.getOrNull(index - 1) ?: return
    val currentOffsets = offsetsSnapshot[previousOperation.id] ?: return

    currentOffsets.forEach { (info, transform) ->
        val pivot = pivotsMap[info]
        pivot?.apply {
            position = transform.positionInContainer
            quaternion = transform.quaternionInContainer
        }
    }
}

/**
 * Apply offsets of all operations passed depending on global boolean Operation.isGlobal
 */
fun applyOperationOffsets(
    operationTargets: List<OperationTargets>,
    renderableInfoStates: Map<PivotInfo, RestorableState>,
    containersMap: Map<Int, ContainerNode> // Map<Model.id, ContainerNode>
) {
    operationTargets.forEach { operationTarget ->
        operationTarget.targets.forEach { renderableTarget ->
            val containerNode = containersMap[renderableTarget.modelId] ?: return@forEach
            val info = PivotInfo(renderableTarget.modelId, renderableTarget.xxh3)
            val state = renderableInfoStates[info] ?: return@forEach

            containerNode.pivotNodes.getOrNull(state.index)?.apply {
                if (operationTarget.operation.isGlobal) {
                    applyGlobalPositionOffset(operationTarget.operation.containerOffsetPosition)
                    applyGlobalQuaternionOffset(operationTarget.operation.containerOffsetQuaternion)
                } else {
                    applyObjectPositionOffset(operationTarget.operation.containerOffsetPosition)
                    applyObjectQuaternionOffset(operationTarget.operation.containerOffsetQuaternion)
                }
            }
        }
    }
}

/**
 * Apply offsets using the reference lookup for O(1) access
 */
fun applyOperationOffsets2(
    operationTargets: List<OperationTargets>,
    pivotsLookup: Map<PivotInfo, PivotNode>
) {
    operationTargets.forEach { operationTarget ->
        operationTarget.targets.forEach { renderableTarget ->
            val info = PivotInfo(renderableTarget.modelId, renderableTarget.xxh3)
            val pivotNode = pivotsLookup[info] ?: return@forEach

            if (operationTarget.operation.isGlobal) {
                pivotNode.applyGlobalPositionOffset(operationTarget.operation.containerOffsetPosition)
                pivotNode.applyGlobalQuaternionOffset(operationTarget.operation.containerOffsetQuaternion)
            } else {
                pivotNode.applyObjectPositionOffset(operationTarget.operation.containerOffsetPosition)
                pivotNode.applyObjectQuaternionOffset(operationTarget.operation.containerOffsetQuaternion)
            }
        }
    }
}


fun applyOperationsOffsetsBeforeTo(
    currentOperation: Operation?,
    steps: List<Step>,
    operationsTargets: List<OperationTargets>,
    renderableInfoStates: Map<PivotInfo, RestorableState>,
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

fun applyOperationsOffsetsBeforeTo2(
    currentOperation: Operation?,
    steps: List<Step>,
    operationsTargets: List<OperationTargets>,
    pivotsLookup: Map<PivotInfo, PivotNode>
) {
    restorePivotsTransforms2(pivotsLookup)

    val isNewOperation = (currentOperation?.id ?: return) == 0
    val stepsOrdersMap = steps.associate { it.id to it.order }
    val currentStepOrder = stepsOrdersMap[currentOperation.stepId] ?: return

    val operationTargetsFiltered = operationsTargets
        .sortedWith(compareBy({ stepsOrdersMap[it.operation.stepId] }, { it.operation.order }))
        .takeWhile { operationTarget ->
            val stepOrder =
                stepsOrdersMap[operationTarget.operation.stepId] ?: return@takeWhile false
            if (operationTarget.operation.id == 0) return@takeWhile false

            stepOrder < currentStepOrder ||
                    (stepOrder == currentStepOrder && isNewOperation) ||
                    (stepOrder == currentStepOrder && operationTarget.operation.order < currentOperation.order)
        }

    applyOperationOffsets2(
        operationTargets = operationTargetsFiltered,
        pivotsLookup = pivotsLookup
    )
}

suspend fun launchOperationAnimation(
    currentOperation: Operation,
    selectedPivots: List<PivotNode>,
    offsetsSnapshot: Map<OperationId, PivotsOffsetMap>,
    pivotsMap: Map<PivotInfo, PivotNode>,
    isEditionEnabled: Boolean,
    currentSpeed: Speed,
    isPlaying: Boolean,
    isLoopingEnabled: Boolean,
) {
    val nodesToAnimate: List<PivotNode> = if(isEditionEnabled) {
        selectedPivots
    } else {
        val pivotOffsets = offsetsSnapshot[currentOperation.id] ?: return
        pivotOffsets.keys.mapNotNull{
            pivotsMap[it]
        }
    }

    coroutineScope {
        nodesToAnimate.map { pivotNode ->
            launch {
                pivotNode.animate(
                    operation = currentOperation,
                    initialPosition = pivotNode.position,
                    initialQuaternion = pivotNode.quaternion,
                    speed = currentSpeed,
                    isPlaying = isPlaying,
                    isLoopingEnabled = isLoopingEnabled,
                )
            }
        }.joinAll()
    }
}

suspend fun launchOperationAnimation(
    currentOperation: Operation,
    operationsTargets: List<OperationTargets>,
    renderableInfoStates: Map<PivotInfo, RestorableState>,
    selectedRenderableInfoStates: Map<PivotInfo, RestorableState>,
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
                val info = PivotInfo(target.modelId, target.xxh3)
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

suspend fun launchOperationAnimation2(
    currentOperation: Operation,
    operationsTargets: List<OperationTargets>,
    pivotsLookup: Map<PivotInfo, PivotNode>,
    selectedPivots: List<PivotNode>,
    isEditionEnabled: Boolean,
    currentSpeed: Speed,
    isPlaying: Boolean,
    isLoopingEnabled: Boolean,
) {
    val nodesToAnimate: List<PivotNode> = if (isEditionEnabled) {
        selectedPivots
    } else {
        operationsTargets
            .find { it.operation.id == currentOperation.id }
            ?.targets
            ?.mapNotNull { target ->
                val info = PivotInfo(target.modelId, target.xxh3)
                pivotsLookup[info]
            } ?: emptyList()
    }

    coroutineScope {
        nodesToAnimate.map { pivotNode ->
            launch {
                pivotNode.animate2(
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
    isLoopingEnabled: Boolean
) {
    val finalPosition =
        calculateWorldPosition(operation.containerOffsetPosition, operation.isGlobal)
    val finalQuaternion =
        calculateWorldQuaternion(operation.containerOffsetQuaternion, operation.isGlobal)

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
        if (operation.isGlobal) {
            unidirectionalRotation(operation.axis, ratio * totalDegrees) * initialQuaternion
        } else {
            initialQuaternion * unidirectionalRotation(operation.axis, ratio * totalDegrees)
        }
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

suspend fun PivotNode.animate2(
    operation: Operation,
    initialPosition: Position,
    initialQuaternion: Quaternion,
    speed: Speed,
    isPlaying: Boolean,
    isLoopingEnabled: Boolean
) {
    val finalPosition =
        calculateWorldPosition(operation.containerOffsetPosition, operation.isGlobal)
    val finalQuaternion =
        calculateWorldQuaternion(operation.containerOffsetQuaternion, operation.isGlobal)

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
        if (operation.isGlobal) {
            unidirectionalRotation(operation.axis, ratio * totalDegrees) * initialQuaternion
        } else {
            initialQuaternion * unidirectionalRotation(operation.axis, ratio * totalDegrees)
        }
    } else {
        slerp(initialQuaternion, finalQuaternion, ratio)
    }

    while (true) {
        // Delay
        while (delayTimeMs < baseDelayMs) {
            worldPosition = initialPosition
            worldQuaternion = initialQuaternion
            updateMs { delayTimeMs += it }
            if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
            yield()
        }

        // Animation
        while (animationTimeMs < baseDurationMs) {
            updateMs { animationTimeMs += it }
            val ratio = (animationTimeMs / baseDurationMs).coerceIn(0F..1F)
            worldPosition = lerp(initialPosition, finalPosition, ratio)
            worldQuaternion = animationQuaternion(ratio)
            if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
            yield()
        }

        worldPosition = finalPosition
        worldQuaternion = finalQuaternion

        if (isLoopingEnabled) {
            // Post Delay
            while (postDelayTimeMs < postDelayMs) {
                updateMs { postDelayTimeMs += it }
                if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
                yield()
            }
            // Reverse
            while (reverseTimeMs < baseDurationMs) {
                updateMs { reverseTimeMs += it }
                val ratio = 1 - (reverseTimeMs / baseDurationMs).coerceIn(0F..1F)
                worldPosition = lerp(initialPosition, finalPosition, ratio)
                worldQuaternion = animationQuaternion(ratio)
                if (!isPlaying) pauseUntilResumed { lastNanos = 0L }
                yield()
            }
            delayTimeMs = 0F; animationTimeMs = 0F; postDelayTimeMs = 0F; reverseTimeMs =
                0F; lastNanos = 0L
        } else {
            return
        }
    }
}