package com.jssdvv.ara.machines.data.local.mapper

import com.jssdvv.ara.machines.data.local.entity.ToolEntity
import com.jssdvv.ara.machines.domain.model.Tool

fun ToolEntity.toDomain() = Tool(
    id = id,
    type = type,
    name = name,
    code = code,
    bodyMediaUri = bodyMediaUri,
    symbolMediaUri = symbolMediaUri,
)

fun Tool.toEntity() = ToolEntity(
    id = id,
    type = type,
    name = name,
    code = code,
    bodyMediaUri = bodyMediaUri,
    symbolMediaUri = symbolMediaUri,
)