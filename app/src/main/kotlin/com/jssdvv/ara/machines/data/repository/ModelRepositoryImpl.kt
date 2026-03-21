package com.jssdvv.ara.machines.data.repository

import com.jssdvv.ara.machines.data.local.dao.ModelDao
import com.jssdvv.ara.machines.data.local.entity.ModelEntity
import com.jssdvv.ara.machines.data.local.mapper.toDomain
import com.jssdvv.ara.machines.data.local.mapper.toEntity
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ModelRepositoryImpl(
    private val dao: ModelDao,
) : ModelRepository {

    override fun countModelsByMachineId(machineId: Int): Flow<Int> =
        dao.countModelsByMachineId(machineId)

    override fun selectModelsByMachineId(machineId: Int): Flow<List<Model>> =
        dao.selectModelsByMachineId(machineId).map { it.map(ModelEntity::toDomain) }

    override suspend fun upsertModel(vararg model: Model) =
        dao.upsertModels(*model.map(Model::toEntity).toTypedArray())

    override suspend fun deleteModel(vararg model: Model) =
        dao.deleteModels(*model.map(Model::toEntity).toTypedArray())
}