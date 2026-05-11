package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import com.jssdvv.ara.machines.domain.type.ToolType

data class Tool(
    val id: Int = 0,
    val type: ToolType,
    val name: String,
    val code: String?,
    val bodyMediaUri: Uri?,
    val symbolMediaUri: Uri?,
)