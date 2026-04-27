package com.jssdvv.ara.machines.presentation.sceneview.utility

import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.node.Node

fun Node.offset(offset: Position, global: Boolean = false): Position {
    return worldPosition.offset(offset, worldQuaternion, global)
}

fun Node.offset(offset: Quaternion, global: Boolean = false): Quaternion {
    return worldQuaternion.offset(offset, global)
}

fun Node.offset(offset: Transform, global: Boolean = false): Transform {
    return worldTransform.offset(offset, global)
}

fun Node.applyOffset(offset: Position, global: Boolean = false) {
    worldPosition = offset(offset, global)
}

fun Node.applyOffset(offset: Quaternion, global: Boolean = false) {
    worldQuaternion = offset(offset, global)
}

fun Node.applyOffset(offset: Transform, global: Boolean = false) {
    worldTransform = offset(offset, global)
}