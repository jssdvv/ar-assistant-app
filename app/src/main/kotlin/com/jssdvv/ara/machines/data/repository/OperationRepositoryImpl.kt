package com.jssdvv.ara.machines.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.data.local.dao.OperationDao
import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.mapper.operation.toComposite
import com.jssdvv.ara.machines.data.local.mapper.operation.toDomain
import com.jssdvv.ara.machines.data.local.mapper.operation.toEntity
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OperationRepositoryImpl(
    private val dao: OperationDao
) : OperationRepository {
    override fun selectOperationsWithTargetsByStepsIdsOrdered(
        stepsIds: List<Int>,
        orderType: OrderType
    ): Flow<List<OperationTargets>> {
        val placeHolders = stepsIds.joinToString(",") { "?" }
        val query =
            """
            SELECT * FROM ${OperationEntity.TABLE_NAME}
            WHERE ${OperationEntity.COLUMN_STEP_ID} IN ($placeHolders)
            ORDER BY `${OperationEntity.COLUMN_ORDER}` ${orderType.queryString}
            """.trimIndent()
        val simpleSQLiteQuery = SimpleSQLiteQuery(query, stepsIds.toTypedArray())
        return dao.selectOperationsWithTargetsByStepsIdsOrdered(simpleSQLiteQuery)
            .map { it.map(OperationWithTargets::toDomain) }
    }

    override suspend fun upsertOperationTargets(vararg model: OperationTargets) {
        model.forEach { operationTargets ->
            val entity = operationTargets.operation.toEntity()
            val result = dao.upsertOperation(entity)[0].toInt()
            val generatedOperationId = if (result == -1) entity.id else result

            val newTargets = operationTargets.pivots.map {
                it.toComposite(generatedOperationId)
            }

            val existingTargets = dao.selectTargetsByOperationId(generatedOperationId)
            val targetsToDelete = existingTargets.filter { existingTarget ->
                newTargets.none { newTarget ->
                    newTarget.modelId == existingTarget.modelId &&
                            newTarget.hash == existingTarget.hash
                }
            }

            if (targetsToDelete.isNotEmpty()) {
                dao.deleteTargets(targetsToDelete)
            }

            dao.upsertTargets(newTargets)
        }
    }

    override suspend fun upsertOperation(vararg model: Operation) {
        dao.upsertOperation(*model.map { it.toEntity() }.toTypedArray())
    }

    override suspend fun deleteOperation(vararg model: Operation) =
        dao.deleteOperation(*model.map { it.toEntity() }.toTypedArray())
}