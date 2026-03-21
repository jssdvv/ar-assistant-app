package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import com.jssdvv.ara.machines.domain.type.ActivityType
import java.util.Date

data class Activity(
    val id: Int = 0,
    val machineId: Int,
    val name: String,
    val type: ActivityType = ActivityType.OTHER,
    val description: String? = null,
    val frequency: Int? = null,
    val frequencyUnit: String? = null,
    val imageUri: Uri? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)