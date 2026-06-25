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
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_PLAYING_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_SELECTED_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.MODEL_UNSELECTED_COLOR
import com.jssdvv.ara.machines.presentation.sceneview.utility.createModelMaterial
import com.jssdvv.ara.machines.presentation.sceneview.utility.offset
import com.jssdvv.ara.machines.presentation.sceneview.utility.unidirectionalRotation
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

    var isPlaying by mutableStateOf(false)
        private set

    var isSelected by mutableStateOf(false)
        private set

    var initialTransform: Transform = Transform()

    var boxNode: BoxNode? = null
    var gizmoNode: GizmoNode? = null
    var renderableNode: ModelNode.RenderableNode? = null
        set(value) {
            field?.parent = null
            field = value
            value?.parent = this
        }

    fun restoreInitialTransform() {
        transform = initialTransform
    }

    suspend fun animate(
        operation: Operation,
        initialTransform: Transform = transform,
        speed: Speed = Speed.NORMAL,
        playing: Boolean = true,
        looping: Boolean = true
    ) {
        val finalTransform = initialTransform.offset(operation.offsetTransform, operation.global)
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
                position = initialTransform.position
                quaternion = initialTransform.quaternion
                updateMs { delayDeltaMs -> delayTimeMs += delayDeltaMs }
                if (!playing) pauseUntilResumed { lastNanos = 0L }
                yield()
            }

            // Animation
            while (animationTimeMs < baseDurationMs) {
                updateMs { animationDeltaMs -> animationTimeMs += animationDeltaMs }
                val ratio = (animationTimeMs / baseDurationMs).coerceIn(0F..1F)
                position = lerp(initialTransform.position, finalTransform.position, ratio)
                quaternion = animationQuaternion(ratio)

                if (!playing) pauseUntilResumed { lastNanos = 0L }
                yield()
            }

            position = finalTransform.position
            quaternion = finalTransform.quaternion

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
                    position = lerp(initialTransform.position, finalTransform.position, ratio)
                    quaternion = animationQuaternion(ratio)

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

    fun setVisuals(materialLoader: MaterialLoader, selected: Boolean = true) {
        if (selected) {
            generateBoxNode(materialLoader)
            generateGizmoNode(materialLoader)
        }
        boxNode?.isVisible = selected
        gizmoNode?.isVisible = selected
    }

    private fun applyMaterial(materialLoader: MaterialLoader) {
        renderableNode?.materialInstance = materialLoader.createModelMaterial(
            when {
                isSelected -> MODEL_SELECTED_COLOR
                isPlaying -> MODEL_PLAYING_COLOR
                else -> MODEL_UNSELECTED_COLOR
            }
        )
    }

    fun setPlaying(materialLoader: MaterialLoader, playing: Boolean = true) {
        isPlaying = playing
        setVisuals(materialLoader, false)
        applyMaterial(materialLoader)
    }

    fun setSelection(materialLoader: MaterialLoader, selected: Boolean = true) {
        isSelected = selected
        isTouchable = !selected
        setVisuals(materialLoader, selected)
        applyMaterial(materialLoader)
    }

    fun reset(materialLoader: MaterialLoader) {
        isPlaying = false
        isSelected = false
        isTouchable = true
        setVisuals(materialLoader, false)
        applyMaterial(materialLoader)
    }

    fun updateGizmoOrientation(global: Boolean) {
        gizmoNode?.worldQuaternion =
            if (global) parent?.worldQuaternion ?: Quaternion() else this@PivotNode.worldQuaternion
    }

    fun toggleVisibility() {
        val value = !renderableVisible
        renderableVisible = value
        renderableNode?.apply{
            isTouchable = value
            isVisible = value
        }
    }

    init {
        isTouchable = false
    }
}