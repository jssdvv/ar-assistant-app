package com.jssdvv.ara.machines.domain.model

import android.net.Uri

data class Step(
    val id: Int = 0,
    val activityId: Int,
    val name: String,
    val orderNumber: Int,
    val description: String? = null,
    val imageUri: Uri? = null,
)