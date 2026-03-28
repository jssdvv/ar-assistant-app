package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow

data class OperationDataManager(
    val select: SelectOperation,
    val upsert: UpsertOperation,
    val delete: DeleteOperation,
)

class SelectOperation(private val repository: OperationRepository) {
    operator fun invoke(stepsIds: List<Int>, orderType: OrderType): Flow<List<OperationTargets>> =
        repository.selectOperationsWithTargetsByStepsIdsOrdered(stepsIds, orderType)
}

class UpsertOperation(private val repository: OperationRepository) {
    suspend operator fun invoke(vararg operation: Operation) =
        repository.upsertOperation(*operation)

    suspend operator fun invoke(vararg operationsTargets: OperationTargets) =
        repository.upsertOperationTargets(*operationsTargets)
}

class DeleteOperation(private val repository: OperationRepository) {
    suspend operator fun invoke(vararg operations: Operation) =
        repository.deleteOperation(*operations)
}