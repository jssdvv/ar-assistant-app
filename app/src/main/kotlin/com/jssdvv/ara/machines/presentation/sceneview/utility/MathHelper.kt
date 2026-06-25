package com.jssdvv.ara.machines.presentation.sceneview.utility

import com.jssdvv.ara.machines.domain.type.Axis
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion
import kotlin.math.PI
import kotlin.math.atan2

fun Position.objectOffset(offset: Position, quaternion: Quaternion): Position {
    return this + quaternion * offset
}

fun Position.globalOffset(offset: Position): Position {
    return this + offset
}

fun Quaternion.objectOffset(offset: Quaternion): Quaternion {
    return normalize(this * offset)
}

fun Quaternion.globalOffset(offset: Quaternion): Quaternion {
    return normalize(offset * this)
}

fun Transform.objectOffset(offset: Transform) = Transform(
    position = position.objectOffset(offset.position, quaternion),
    quaternion = quaternion.objectOffset(offset.quaternion)
)

fun Transform.objectOffset(offset: Position) = Transform(
    position = position.objectOffset(offset, quaternion),
    quaternion = normalize(quaternion)
)

fun Transform.globalOffset(offset: Transform) = Transform(
    position = position.globalOffset(offset.position),
    quaternion = quaternion.globalOffset(offset.quaternion)
)

fun Transform.globalOffset(offset: Position) = Transform(
    position = position.globalOffset(offset),
    quaternion = normalize(quaternion)
)

fun Position.offset(offset: Position, quaternion: Quaternion, global: Boolean = false): Position {
    return if (global) globalOffset(offset) else objectOffset(offset, quaternion)
}

fun Quaternion.offset(offset: Quaternion, global: Boolean = false): Quaternion {
    return if (global) globalOffset(offset) else objectOffset(offset)
}

fun Transform.offset(offset: Transform, global: Boolean = false): Transform {
    return if (global) globalOffset(offset) else objectOffset(offset)
}

fun Quaternion.degreesFromAxis(axis: Axis): Float {
    val sinHalfTheta = dot(xyz, axis.unitVector)
    val radians = 2 * atan2(sinHalfTheta.toDouble(), w.toDouble())
    return (radians * 180 / PI).toFloat()
}

fun unidirectionalTranslation(axis: Axis, value: Float) = axis.unitVector * value

fun unidirectionalRotation(axis: Axis, degrees: Float) =
    Quaternion.fromEuler(axis.unitVector * degrees)