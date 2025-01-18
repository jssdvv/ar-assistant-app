package com.jssdvv.ara.core.domain.model

data class Model(
    val modelId: Int,
    val machineId: Int,
    val name: String,
    val fileUri: String,
    val description: String,
    val timestamp: Long,
    val positionX: Float,
    val positionY: Float,
    val positionZ: Float,
    val rotationX: Float,
    val rotationY: Float,
    val rotationZ: Float,
)