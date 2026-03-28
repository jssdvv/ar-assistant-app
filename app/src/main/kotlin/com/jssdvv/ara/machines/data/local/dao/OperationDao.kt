package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.RenderableTargetComposite
import com.jssdvv.ara.machines.data.local.relation.OperationWithTargets
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {
    @Transaction
    @RawQuery(observedEntities = [OperationEntity::class, RenderableTargetComposite::class])
    fun selectOperationsWithTargetsByStepsIdsOrdered(
        query: SupportSQLiteQuery
    ) : Flow<List<OperationWithTargets>>

    @Upsert
    suspend fun upsertOperation(vararg entity: OperationEntity) : List<Long>

    @Delete
    suspend fun deleteOperation(vararg entity: OperationEntity)

    @Upsert
    suspend fun upsertTargets(composites: List<RenderableTargetComposite>)

    @Delete
    suspend fun deleteTargets(composites: List<RenderableTargetComposite>)

    @Transaction
    @Query(
        """
        SELECT * FROM ${RenderableTargetComposite.TABLE_NAME}
        WHERE ${RenderableTargetComposite.COLUMN_OPERATION_ID} = :operationId
        """
    )
    suspend fun selectTargetsByOperationId(operationId: Int): List<RenderableTargetComposite>

    @Query(
        """
        DELETE FROM ${RenderableTargetComposite.TABLE_NAME} WHERE
        ${RenderableTargetComposite.COLUMN_OPERATION_ID} = :operationId
        """
    )
    suspend fun deleteTargetsByOperationId(operationId: Int)
}