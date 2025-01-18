package com.jssdvv.ara.core.domain.model

import kotlinx.serialization.json.Json

data class Step(
    val stepId: Int,
    val activityId: Int,
    val name: String,
    val orderNumber: String,
    val description: String,
    val models: String,
    val tools: String,
    val parts: String,
)