package com.jssdvv.ara.core.domain.model

import android.net.Uri

data class Machine(
    val machineId: Int = 0,
    val name: String,
    val category: String,
    val imageUri: Uri? = null,
    val description: String,
    val timestamp: Long,
)