package com.jssdvv.ara.core.domain.model

import android.net.Uri

data class Activity(
    val activityId: Int,
    val machineId: Int,
    val category: String,
    val name: String,
    val imageUri: Uri?,
    val description: String,
    val frequency: String?,
    val frequencyUnit: String?,
    val timestamp: Long,
)