package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.TargetRenderableComposite
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {
    @Transaction
    @RawQuery(observedEntities = [OperationEntity::class, TargetRenderableComposite::class])
    fun selectOperationsWithTargetsByStepsIdsOrdered(
        query: SupportSQLiteQuery
    ) : Flow<List<OperationWithTargets>>

    @Upsert
    suspend fun upsertOperation(entity: OperationEntity) : Long

    @Delete
    suspend fun deleteOperation(vararg entity: OperationEntity)

    @Upsert
    suspend fun upsertTargets(composites: List<TargetRenderableComposite>)

    @Transaction
    @Query(
        """
        SELECT * FROM ${TargetRenderableComposite.TABLE_NAME}
        WHERE ${TargetRenderableComposite.COLUMN_OPERATION_ID} = :operationId
        """
    )
    suspend fun selectTargetsByOperationId(operationId: Int): List<TargetRenderableComposite>

    @Query(
        """
        DELETE FROM ${TargetRenderableComposite.TABLE_NAME} WHERE
        ${TargetRenderableComposite.COLUMN_OPERATION_ID} = :operationId
        """
    )
    suspend fun deleteTargetsByOperationId(operationId: Int)

    @Transaction
    suspend fun replaceTargets(operationId: Int, targets: List<TargetRenderableComposite>) {
        deleteTargetsByOperationId(operationId)
        upsertTargets(targets)
    }
}