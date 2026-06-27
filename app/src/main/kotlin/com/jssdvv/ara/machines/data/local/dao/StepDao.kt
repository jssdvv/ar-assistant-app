package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.jssdvv.ara.machines.data.local.entity.StepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    @RawQuery(observedEntities = [StepEntity::class])
    fun selectStepsOrdered(query: SupportSQLiteQuery): Flow<List<StepEntity>>

    @Query(
        """
        SELECT * FROM ${StepEntity.TABLE_NAME}
        WHERE ${StepEntity.COLUMN_ID} = :id
        """
    )
    fun selectStepById(id: Int): Flow<StepEntity?>

    @Upsert
    suspend fun upsertStep(vararg entity: StepEntity): List<Long>

    @Delete
    suspend fun deleteStep(vararg entity: StepEntity)
}
