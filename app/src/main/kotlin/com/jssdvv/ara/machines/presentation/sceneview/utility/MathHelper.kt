package com.jssdvv.ara.machines.presentation.sceneview.utility

import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.normalize
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion

fun Position.objectOffset(offset: Position, quaternion: Quaternion) = this + quaternion * offset
fun Position.globalOffset(offset: Position) = this + offset

fun Quaternion.objectOffset(offset: Quaternion) = normalize(this * offset)
fun Quaternion.globalOffset(offset: Quaternion) = normalize(offset * this)

fun Transform.objectOffset(offset: Transform) = Transform(
    position = position.objectOffset(offset.position, quaternion),
    quaternion = quaternion.objectOffset(offset.quaternion)
)
fun Transform.globalOffset(offset: Transform) = Transform(
    position = position.globalOffset(offset.position),
    quaternion = quaternion.globalOffset(offset.quaternion)
)

fun Position.offset(offset: Position, quaternion: Quaternion, global: Boolean = false): Position {
    return if (global) globalOffset(offset) else objectOffset(offset, quaternion)
}

fun Quaternion.offset(offset: Quaternion, global: Boolean = false): Quaternion {
    return if (global) globalOffset(offset) else objectOffset(offset)
}

fun Transform.offset(offset: Transform, global: Boolean = false): Transform {
    return if ( global) globalOffset(offset) else objectOffset(offset)
}