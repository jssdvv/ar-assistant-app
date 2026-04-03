package com.jssdvv.ara.machines.domain.utility

import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode

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
    val tempIndex: Int
    val initialPosition: Position
    val initialQuaternion: Quaternion
}

fun restoreRenderablesTransform(
    renderableInfoStates: Map<RenderableInfo, RestorableState>,
    modelNodesMap: Map<String?, ModelNode> // Map<ModelNode.name, ModelNode>
) {
    renderableInfoStates.forEach { (renderable, state) ->
        val modelNode = modelNodesMap[renderable.modelId.toString()] ?: return@forEach
        modelNode.renderableNodes.getOrNull(state.tempIndex)?.apply {
            position = state.initialPosition
            quaternion = state.initialQuaternion
        }
    }
}

fun applyOperationOffsets(
    operationTargets: List<OperationTargets>,
    renderableInfoStates: Map<RenderableInfo, RestorableState>,
    modelNodesMap: Map<String?, ModelNode>
) {
    operationTargets.forEach { operationTarget ->
        operationTarget.targets.forEach { target ->
            val modelNode = modelNodesMap[target.modelId.toString()] ?: return@forEach
            val renderableInfo = RenderableInfo(target.modelId, target.xxh3)
            val state = renderableInfoStates[renderableInfo] ?: return@forEach

            modelNode.renderableNodes.getOrNull(state.tempIndex)?.apply {
                if(operationTarget.operation.isGlobal) {
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
    operation: Operation?,
    steps: List<Step>,
    operationsTargets: List<OperationTargets>,
    renderableInfoStates: Map<RenderableInfo, RestorableState>,
    modelNodesMap: Map<String?, ModelNode>
) {
    restoreRenderablesTransform(
        renderableInfoStates = renderableInfoStates,
        modelNodesMap = modelNodesMap
    )

    val isNewOperation = (operation?.id ?: return) == 0
    val stepsOrderMap = steps.associate { it.id to it.order }
    val targetStepOrder = stepsOrderMap[operation.stepId] ?: return

    val operationTargetsFiltered = operationsTargets
        .sortedWith(
            compareBy(
                { stepsOrderMap[it.operation.stepId] },
                { it.operation.order }
            )
        )
        .takeWhile { operationTarget ->
            val stepOrder =
                stepsOrderMap[operationTarget.operation.stepId] ?: return@takeWhile false

            if (operationTarget.operation.id == 0) return@takeWhile false

            stepOrder < targetStepOrder ||
            (stepOrder == targetStepOrder && isNewOperation) ||
            (stepOrder == targetStepOrder && operationTarget.operation.order < operation.order)
        }

    applyOperationOffsets(
        operationTargets = operationTargetsFiltered,
        renderableInfoStates = renderableInfoStates,
        modelNodesMap = modelNodesMap
    )
}