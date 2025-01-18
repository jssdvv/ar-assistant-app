package com.jssdvv.ara.core.data.repository

import com.jssdvv.ara.core.data.local.dao.ModelDao
import com.jssdvv.ara.core.data.mapper.toDomain
import com.jssdvv.ara.core.data.mapper.toEntity
import com.jssdvv.ara.core.domain.model.Model
import com.jssdvv.ara.core.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ModelRepositoryImpl(
    private val dao: ModelDao,
) : ModelRepository {
    override fun getAllModelsForMachineIdAsc(machineId: Int): Flow<List<Model>> {
        return dao.getAllModelsForMachineIdAsc(machineId).map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun insertModel(model: Model) {
        return dao.insertModel(model.toEntity())
    }

    override suspend fun updateModel(model: Model) {
        return dao.updateModel(model.toEntity())
    }

    override suspend fun deleteModel(model: Model) {
        return dao.deleteModel(model.toEntity())
    }
}