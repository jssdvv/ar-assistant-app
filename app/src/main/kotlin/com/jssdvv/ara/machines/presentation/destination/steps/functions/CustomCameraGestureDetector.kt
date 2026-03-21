package com.jssdvv.ara.machines.presentation.destination.steps.functions

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.google.android.filament.utils.Manipulator
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.distance
import dev.romainguy.kotlin.math.mix
import io.github.sceneview.gesture.CameraGestureDetector
import io.github.sceneview.gesture.orbitHomePosition
import io.github.sceneview.gesture.targetPosition
import io.github.sceneview.gesture.transform
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform

class CustomCameraGestureDetector(
    private val viewHeight: () -> Int,
    var cameraManipulator: CameraGestureDetector.CameraManipulator?
) {
    class CameraManipulator(
        val manipulator: Manipulator
    ) : CameraGestureDetector.CameraManipulator {

        private val zoomSpeed = 0.1F

        constructor(
            orbitHomePosition: Position? = null,
            targetPosition: Position? = null
        ) : this(
            Manipulator.Builder()
                .apply {
                    orbitHomePosition?.let { orbitHomePosition(it) }
                    targetPosition?.let { targetPosition(it) }
                }
                .orbitSpeed(0.005F, 0.005F)
                .zoomSpeed(0.03F)
                .flightPanSpeed(0.05F, 0.05F)
                .build(Manipulator.Mode.ORBIT)
        )

        override fun getTransform(): Transform = manipulator.transform

        override fun grabBegin(x: Int, y: Int, strafe: Boolean) =
            manipulator.grabBegin(x, y, strafe)

        override fun grabEnd() = manipulator.grabEnd()

        override fun grabUpdate(x: Int, y: Int) = manipulator.grabUpdate(x, y)

        override fun scrollBegin(x: Int, y: Int, separation: Float) {}

        override fun scrollEnd() {}

        override fun scrollUpdate(x: Int, y: Int, prevSeparation: Float, currSeparation: Float) =
            manipulator.scroll(x, y, (prevSeparation - currSeparation) * zoomSpeed)

        override fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

        override fun update(deltaTime: Float) = manipulator.update(deltaTime)
    }

    private enum class Gesture { NONE, ZOOM, PAN, ORBIT }

    private data class TouchPair(
        var pointer0: Float2 = Float2(),
        var pointer1: Float2 = Float2(),
        var pointerCount: Int = 0,
        var time: Long = 0L
    ) {
        constructor(event: MotionEvent, height: Int) : this(time = event.eventTime) {
            if (event.pointerCount >= 2) {
                pointer0 = Float2(event.getX(0), height - event.getY(0))
                pointer1 = Float2(event.getX(1), height - event.getY(1))
                pointerCount = 2
            } else if (event.pointerCount >= 1) {
                pointer0 = Float2(event.getX(0), height - event.getY(0))
                pointer1 = pointer0
                pointerCount = 1
            }
        }

        val separation get() = distance(pointer0, pointer1)
        val midpoint get() = mix(pointer0, pointer1, 0.5F)
        val x get() = midpoint.x.toInt()
        val y get() = midpoint.y.toInt()
    }

    private var currentGesture = Gesture.NONE
    private var tapHistory = ArrayDeque<TouchPair>(2) // One pointer TouchPair list
    private val kGestureConfidenceCount = 2 // Number of events to consider a 2 pointer gesture
    private val kDoubleTapTimeout = 400L // In millis
    private val kDoubleTapSlop = 60 // In pixels

    // On Action Move
    private var possibleZoomEvents = 0 // Double Pointer Touches
    private var previousTouch: TouchPair? = null

    fun onTouchEvent(event: MotionEvent) {
        val touch = TouchPair(event, viewHeight())
        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                // Avoid to save a second pointer down when there is already a pointer down
                if (event.pointerCount == 1) tapHistory.pushFront(touch)
            }

            MotionEvent.ACTION_MOVE -> {
                // Cancel gesture due to unexpected pointer count
                if ((event.pointerCount != 1 && currentGesture == Gesture.PAN) ||
                    (event.pointerCount != 1 && currentGesture == Gesture.ORBIT) ||
                    (event.pointerCount < 2 && currentGesture == Gesture.ZOOM)
                ) {
                    endGesture()
                    return
                }

                // Update existing gesture
                when (currentGesture) {
                    Gesture.ZOOM -> {
                        previousTouch?.let { prev ->
                            cameraManipulator?.scrollUpdate(
                                touch.x,
                                touch.y,
                                prev.separation,
                                touch.separation
                            )
                        }
                        previousTouch = touch
                        return
                    }

                    Gesture.PAN,
                    Gesture.ORBIT -> {
                        cameraManipulator?.grabUpdate(touch.x, touch.y)
                        return
                    }

                    else -> {}
                }

                // Detect new gestures
                if (event.pointerCount == 2) possibleZoomEvents++

                when {
                    isZoomGesture() -> {
                        currentGesture = Gesture.ZOOM
                        cameraManipulator?.scrollBegin(touch.x, touch.y, touch.separation)
                        previousTouch = touch
                        return
                    }

                    isPanGesture(touch) -> {
                        currentGesture = Gesture.PAN
                        cameraManipulator?.grabBegin(touch.x, touch.y, true)
                        return
                    }

                    isOrbitGesture(touch) -> {
                        currentGesture = Gesture.ORBIT
                        cameraManipulator?.grabBegin(touch.x, touch.y, false)
                        return
                    }
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_CANCEL -> {
                endGesture()
            }
        }
    }

    private fun endGesture() {
        possibleZoomEvents = 0
        previousTouch = null
        currentGesture = Gesture.NONE
        cameraManipulator?.grabEnd()
    }

    private fun isZoomGesture(): Boolean = possibleZoomEvents > kGestureConfidenceCount

    private fun isPanGesture(currentTouch: TouchPair): Boolean {
        if (currentTouch.pointerCount > 1) return false
        if (tapHistory.size < 2) return false
        val newest = tapHistory[0]
        val oldest = tapHistory[1]
        val timeBetweenTaps = newest.time - oldest.time
        if (timeBetweenTaps > kDoubleTapTimeout) return false
        val distance = distance(newest.midpoint, oldest.midpoint)
        return distance <= kDoubleTapSlop
    }

    private fun isOrbitGesture(currentTouch: TouchPair): Boolean = currentTouch.pointerCount == 1
}

@Composable
fun rememberCustomCameraManipulator(
    orbitHomePosition: Position? = null,
    targetPosition: Position? = null,
    creator: () -> CameraGestureDetector.CameraManipulator = {
        CustomCameraGestureDetector.CameraManipulator(
            orbitHomePosition,
            targetPosition
        )
    }
) = remember(creator).also { collisionSystem ->
    DisposableEffect(collisionSystem) { onDispose {} }
}

internal fun <T> ArrayDeque<T>.pushFront(value: T) {
    addFirst(value)
    if (size > 2) removeLast()
}