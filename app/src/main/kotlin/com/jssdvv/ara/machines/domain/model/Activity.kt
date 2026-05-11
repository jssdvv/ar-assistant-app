package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import com.jssdvv.ara.machines.domain.type.ActivityType
import java.time.Instant

data class Activity(
    val id: Int = 0,
    val machineId: Int,
    val name: String,
    val type: ActivityType = ActivityType.OTHER,
    val description: String? = null,
    val frequency: Int? = null,
    val frequencyUnit: String? = null,
    val imageUri: Uri? = null,
    val createdAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now()
)