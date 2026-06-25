package com.jssdvv.ara.tools.domain.repository

import com.jssdvv.ara.machines.domain.model.Tool
import kotlinx.coroutines.flow.Flow

interface ToolsRepository {
    fun selectAllTools(): Flow<List<Tool>>
    suspend fun upsertTool(vararg model: Tool)
    suspend fun deleteTool(vararg model: Tool)
}