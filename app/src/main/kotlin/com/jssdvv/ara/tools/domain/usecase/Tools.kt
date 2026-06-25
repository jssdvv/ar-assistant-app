package com.jssdvv.ara.tools.domain.usecase

import com.jssdvv.ara.machines.domain.model.Tool
import com.jssdvv.ara.tools.domain.repository.ToolsRepository
import kotlinx.coroutines.flow.Flow

data class ToolsDataManager(
    val select: SelectTools,
    val upsert: UpsertTools,
    val delete: DeleteTools,
)

class SelectTools(private val repository: ToolsRepository) {
    operator fun invoke(): Flow<List<Tool>> =
        repository.selectAllTools()
}

class UpsertTools(private val repository: ToolsRepository) {
    suspend operator fun invoke(vararg model: Tool) = repository.upsertTool(*model)
}

class DeleteTools(private val repository: ToolsRepository) {
    suspend operator fun invoke(vararg model: Tool) = repository.deleteTool(*model)
}