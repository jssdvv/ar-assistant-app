package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow

data class ModelsDataManager(
    val select: SelectModels,
    val upsert: UpsertModels,
    val delete: DeleteModels,
)

class CountModels(private val repository: ModelRepository) {
    operator fun invoke(machineId: Int): Flow<Int> =
        repository.countModelsByMachineId(machineId)
}

class SelectModels(private val repository: ModelRepository) {
    operator fun invoke(machineId: Int): Flow<List<Model>> =
        repository.selectModelsByMachineId(machineId)
}

class UpsertModels(private val repository: ModelRepository) {
    suspend fun upsertModels(vararg model: Model) =
        repository.upsertModel(*model)
}

class DeleteModels(private val repository: ModelRepository) {
    suspend fun deleteModels(vararg model: Model) =
        repository.deleteModel(*model)
}