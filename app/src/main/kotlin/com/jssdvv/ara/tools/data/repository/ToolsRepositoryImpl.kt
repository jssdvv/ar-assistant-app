package com.jssdvv.ara.tools.data.repository

import com.jssdvv.ara.machines.data.local.entity.ToolEntity
import com.jssdvv.ara.machines.data.local.mapper.toDomain
import com.jssdvv.ara.machines.data.local.mapper.toEntity
import com.jssdvv.ara.machines.domain.model.Tool
import com.jssdvv.ara.tools.data.local.ToolDao
import com.jssdvv.ara.tools.domain.repository.ToolsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ToolsRepositoryImpl(
    private val dao: ToolDao
) : ToolsRepository {
    override fun selectAllTools(): Flow<List<Tool>> =
        dao.selectAllTools().map { it.map(ToolEntity::toDomain) }

    override suspend fun upsertTool(vararg model: Tool) =
        dao.upsertTools(*model.map(Tool::toEntity).toTypedArray())

    override suspend fun deleteTool(vararg model: Tool) =
        dao.deleteTools(*model.map(Tool::toEntity).toTypedArray())
}