package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jssdvv.ara.machines.data.local.entity.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {

    @Query(
        """
        SELECT COUNT(*) FROM ${ModelEntity.TABLE_NAME}
        WHERE ${ModelEntity.COLUMN_MACHINE_ID} = :machineId
        """
    )
    fun countModelsByMachineId(machineId: Int): Flow<Int>

    @Query(
        """
        SELECT * FROM ${ModelEntity.TABLE_NAME}
        WHERE ${ModelEntity.COLUMN_MACHINE_ID} = :machineId
        ORDER BY ${ModelEntity.COLUMN_ID} ASC
        """
    )
    fun selectModelsByMachineId(
        machineId: Int,
    ): Flow<List<ModelEntity>>

    @Upsert
    suspend fun upsertModels(vararg entity: ModelEntity)

    @Delete
    suspend fun deleteModels(vararg entity: ModelEntity)
}