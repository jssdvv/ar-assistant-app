package com.jssdvv.ara.machines.presentation.sceneview.utility

import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import io.github.sceneview.math.Transform
import io.github.sceneview.node.Node

fun Node.offset(offset: Position, global: Boolean = false): Position {
    return position.offset(offset, quaternion, global)
}

fun Node.offset(offset: Quaternion, global: Boolean = false): Quaternion {
    return quaternion.offset(offset, global)
}

fun Node.offset(offset: Transform, global: Boolean = false): Transform {
    return transform.offset(offset, global)
}

fun Node.applyOffset(offset: Position, global: Boolean = false) {
    position = offset(offset, global)
}

fun Node.applyOffset(offset: Quaternion, global: Boolean = false) {
    quaternion = offset(offset, global)
}

fun Node.applyOffset(offset: Transform, global: Boolean = false) {
    transform = offset(offset, global)
}