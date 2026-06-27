package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import kotlinx.coroutines.flow.Flow

interface OperationRepository {
    fun selectOperationsWithTargetsByStepsIdsOrdered(
        stepsIds: List<Int>,
        orderType: OrderType
    ): Flow<List<OperationTargets>>

    suspend fun upsertOperationTargets(vararg model: OperationTargets)
    suspend fun upsertOperation(vararg model: Operation)
    suspend fun deleteOperation(vararg model: Operation)
}