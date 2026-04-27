package com.jssdvv.ara.machines.presentation.sceneview.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import com.google.android.filament.Engine
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.Speed
import com.jssdvv.ara.machines.presentation.destination.steps.functions.unidirectionalRotation
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_PLAYING_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_SELECTED_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_UNSELECTED_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.createModelMaterial
import com.jssdvv.ara.machines.presentation.sceneview.utility.offset
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.slerp
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Transform
import io.github.sceneview.math.halfExtentSize
import io.github.sceneview.math.lerp
import io.github.sceneview.math.quaternion
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.yield

/**
 * Transformation anchor positioned at the geometric
 * center of its [ModelNode.RenderableNode] child.
 */
class PivotNode(engine: Engine) : Node(engine) {
    var modelId: Int = 0
    var hash: Long = 0

    val pivot: Pivot get() = Pivot(modelId, hash)

    var renderableVisible by mutableStateOf(true)
        private set

    var initialTransform: Transform = Transform()

    var boxNode: BoxNode? = null
    var gizmoNode: GizmoNode? = null
    var renderableNode: ModelNode.RenderableNode? = null

    fun restoreTransform() {
        transform = initialTransform
    }

    suspend fun animate(
        operation: Operation,
        initialTransform: Transform = worldTransform,
        speed: Speed = Speed.NORMAL,
        playing: Boolean = true,
        looping: Boolean = true
    ) {
        val finalTransform: Transform = initialTransform.offset(operation.offsetTransform, operation.global)
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
                if (playing) {
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
            snapshotFlow { playing }.first { it }
            onResume()
        }

        fun animationQuaternion(ratio: Float): Quaternion = if (isScrew) {
            if (operation.global) {
                unidirectionalRotation(operation.axis, ratio * totalDegrees) *
                        initialTransform.quaternion
            } else {
                initialTransform.quaternion *
                        unidirectionalRotation(operation.axis, ratio * totalDegrees)
            }
        } else {
            slerp(initialTransform.quaternion, finalTransform.quaternion, ratio)
        }

        while (true) {
            // Delay
            while (delayTimeMs < baseDelayMs) {
                worldPosition = initialTransform.position
                worldQuaternion = initialTransform.quaternion
                updateMs { delayDeltaMs -> delayTimeMs += delayDeltaMs }
                if (!playing) pauseUntilResumed { lastNanos = 0L }
                yield()
            }

            // Animation
            while (animationTimeMs < baseDurationMs) {
                updateMs { animationDeltaMs -> animationTimeMs += animationDeltaMs }
                val ratio = (animationTimeMs / baseDurationMs).coerceIn(0F..1F)
                worldPosition = lerp(initialTransform.position, finalTransform.position, ratio)
                worldQuaternion = animationQuaternion(ratio)

                if (!playing) pauseUntilResumed { lastNanos = 0L }
                yield()
            }

            worldPosition = finalTransform.position
            worldQuaternion = finalTransform.quaternion

            // Post Animation
            if (looping) {

                // Post Delay
                while (postDelayTimeMs < postDelayMs) {
                    updateMs { postDelayDeltaMs -> postDelayTimeMs += postDelayDeltaMs }
                    if (!playing) pauseUntilResumed { lastNanos = 0L }
                    yield()
                }

                // Reverse Animation
                while (reverseTimeMs < baseDurationMs) {
                    updateMs { reverseDeltaMs -> reverseTimeMs += reverseDeltaMs }
                    val ratio = 1 - (reverseTimeMs / baseDurationMs).coerceIn(0F..1F)
                    worldPosition = lerp(initialTransform.position, finalTransform.position, ratio)
                    worldQuaternion = animationQuaternion(ratio)

                    if (!playing) pauseUntilResumed { lastNanos = 0L }
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

    private fun generateBoxNode(materialLoader: MaterialLoader) {
        if (boxNode != null) return
        val size = renderableNode?.axisAlignedBoundingBox?.halfExtentSize?.times(2F) ?: return
        boxNode = BoxNode(engine, size, materialLoader).also { addChildNode(it) }
    }

    private fun generateGizmoNode(materialLoader: MaterialLoader) {
        if (gizmoNode != null) return
        gizmoNode = GizmoNode(engine, materialLoader).also { addChildNode(it) }
    }

    fun setPlaying(materialLoader: MaterialLoader) {
        boxNode?.isVisible = false
        gizmoNode?.isVisible = false
        renderableNode?.materialInstance = materialLoader
            .createModelMaterial(MODEL_PLAYING_COLOR)
    }

    fun setSelection(selected: Boolean, materialLoader: MaterialLoader) {
        val materialColor = if (selected) {
            generateBoxNode(materialLoader)
            generateGizmoNode(materialLoader)
            MODEL_SELECTED_COLOR
        } else {
            MODEL_UNSELECTED_COLOR
        }
        boxNode?.isVisible = selected
        gizmoNode?.isVisible = selected
        renderableNode?.materialInstance = materialLoader.createModelMaterial(materialColor)
    }

    fun updateGizmoQuaternion(global: Boolean) {
        gizmoNode?.worldQuaternion = if (global) Quaternion() else this@PivotNode.worldQuaternion
    }

    fun toggleVisibility() {
        renderableVisible = !renderableVisible
        renderableNode?.isVisible = renderableVisible
    }

    fun setRenderableVisibility(visible: Boolean) {
        this.renderableVisible = visible
        renderableNode?.isVisible = visible
    }

    init {
        isTouchable = false
        isHittable = false
    }
}