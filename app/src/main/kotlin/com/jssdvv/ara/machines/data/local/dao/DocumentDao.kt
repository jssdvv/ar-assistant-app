package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jssdvv.ara.machines.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query(
        """
        SELECT COUNT(*) FROM ${DocumentEntity.TABLE_NAME}
        WHERE ${DocumentEntity.COLUMN_MACHINE_ID} = :machineId
        """
    )
    fun countDocumentsByMachineId(machineId: Int): Flow<Int>

    @Query(
        """
        SELECT * FROM ${DocumentEntity.TABLE_NAME}
        WHERE
            ${DocumentEntity.COLUMN_NAME} = :name AND
            ${DocumentEntity.COLUMN_MACHINE_ID} = :machineId LIMIT 1
        """
    )
    fun selectDocumentByMachineIdAndName(machineId: Int, name: String): DocumentEntity?

    @Query(
        """
        SELECT * FROM ${DocumentEntity.TABLE_NAME}
        WHERE ${DocumentEntity.COLUMN_MACHINE_ID} = :machineId
        ORDER BY ${DocumentEntity.COLUMN_NAME} ASC
        """
    )
    fun selectDocumentsByMachineId(machineId: Int): Flow<List<DocumentEntity>>

    @Upsert
    suspend fun upsertDocument(vararg entity: DocumentEntity)

    @Delete
    suspend fun deleteDocument(vararg entity: DocumentEntity)
}