package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.PivotComposite
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {
    @Transaction
    @RawQuery(observedEntities = [OperationEntity::class, PivotComposite::class])
    fun selectOperationsWithTargetsByStepsIdsOrdered(
        query: SupportSQLiteQuery
    ) : Flow<List<OperationWithTargets>>

    @Upsert
    suspend fun upsertOperation(vararg entity: OperationEntity) : List<Long>

    @Delete
    suspend fun deleteOperation(vararg entity: OperationEntity)

    @Upsert
    suspend fun upsertTargets(composites: List<PivotComposite>)

    @Delete
    suspend fun deleteTargets(composites: List<PivotComposite>)

    @Transaction
    @Query(
        """
        SELECT * FROM ${PivotComposite.TABLE_NAME}
        WHERE ${PivotComposite.COLUMN_OPERATION_ID} = :operationId
        """
    )
    suspend fun selectTargetsByOperationId(operationId: Int): List<PivotComposite>

    @Query(
        """
        DELETE FROM ${PivotComposite.TABLE_NAME} WHERE
        ${PivotComposite.COLUMN_OPERATION_ID} = :operationId
        """
    )
    suspend fun deleteTargetsByOperationId(operationId: Int)
}