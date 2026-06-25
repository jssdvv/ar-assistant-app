package com.jssdvv.ara.tools.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jssdvv.ara.machines.data.local.entity.ToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {

    @Query("SELECT * FROM ${ToolEntity.TABLE_NAME}")
    fun selectAllTools(): Flow<List<ToolEntity>>

    @Upsert
    suspend fun upsertTools(vararg entity: ToolEntity)

    @Delete
    suspend fun deleteTools(vararg entity: ToolEntity)
}