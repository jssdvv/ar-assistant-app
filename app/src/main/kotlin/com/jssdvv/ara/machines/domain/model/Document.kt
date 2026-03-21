package com.jssdvv.ara.machines.domain.model

import android.net.Uri

data class Document(
    val id: Int = 0,
    val machineId: Int,
    val name: String,
    val fileUri: Uri
)