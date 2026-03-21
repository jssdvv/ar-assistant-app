package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.machines.domain.model.Model
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun countModelsByMachineId(machineId: Int): Flow<Int>
    fun selectModelsByMachineId(machineId: Int): Flow<List<Model>>
    suspend fun upsertModel(vararg model: Model)
    suspend fun deleteModel(vararg model: Model)
}