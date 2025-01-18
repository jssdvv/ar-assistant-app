package com.jssdvv.ara.core.data.repository

import com.jssdvv.ara.core.data.local.dao.StepDao
import com.jssdvv.ara.core.data.mapper.toDomain
import com.jssdvv.ara.core.data.mapper.toEntity
import com.jssdvv.ara.core.domain.model.Step
import com.jssdvv.ara.core.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StepRepositoryImpl(
    private val dao: StepDao,
) : StepRepository {
    override fun getAllStepsForActivityIdAsc(activityId: Int): Flow<List<Step>> {
        return dao.getAllStepsForActivityIdAsc(activityId).map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllStepsForActivityIdDesc(activityId: Int): Flow<List<Step>> {
        return dao.getAllStepsForActivityIdDesc(activityId).map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun insertStep(model: Step) {
        return dao.insertStep(model.toEntity())
    }

    override suspend fun updateStep(model: Step) {
        return dao.updateStep(model.toEntity())
    }

    override suspend fun deleteStep(model: Step) {
        return dao.deleteStep(model.toEntity())
    }
}