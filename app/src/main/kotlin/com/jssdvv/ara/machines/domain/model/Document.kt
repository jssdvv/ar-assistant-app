package com.jssdvv.ara.machines.domain.model

import android.net.Uri
import androidx.annotation.StringRes
import com.jssdvv.ara.R
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

enum class DocumentCategory(
    @param:StringRes val labelResId: Int
) {
    UNKNOWN(labelResId = R.string.document_type_unknown_label),
    TECHNICAL_SHEET(labelResId = R.string.document_type_technical_sheet_label),
    MAGAZINE(labelResId = R.string.document_type_magazine_label),
    CATALOG(labelResId = R.string.document_type_catalog_label),
    INVOICE(labelResId = R.string.document_type_invoice_label),
    CONTRACT(labelResId = R.string.document_type_contract_label),
    MANUAL(labelResId = R.string.document_type_manual_label)
}