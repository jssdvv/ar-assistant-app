package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import java.util.Date

data class Document(
    val id: Int = 0,
    val machineId: Int,
    val category: DocumentCategory = DocumentCategory.UNKNOWN,
    val name: String,
    val previewUri: Uri = Uri.EMPTY,
    val fileUri: Uri,
    val createdAt: Date = Date()
)

enum class DocumentCategory {
    UNKNOWN,
    TECHNICAL_SHEET,
    MAGAZINE,
    CATALOG
}