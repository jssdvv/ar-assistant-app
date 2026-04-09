package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.DocumentEntity
import com.jssdvv.ara.machines.domain.model.Document

fun Document.toEntity() = DocumentEntity(
    id = id,
    machineId = machineId,
    name = name,
    category = category,
    previewUri = previewUri,
    fileUri = fileUri,
    createdAt = createdAt
)

fun DocumentEntity.toDomain() = Document(
    id = id,
    machineId = machineId,
    name = name,
    category = category,
    previewUri = previewUri,
    fileUri = fileUri,
    createdAt = createdAt
)