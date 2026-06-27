package com.jssdvv.ara.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jssdvv.ara.schedule.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM ${EventEntity.TABLE_NAME}")
    fun selectAllEvents(): Flow<List<EventEntity>>

    @Upsert
    suspend fun upsertEvents(entity: EventEntity): Long

    @Delete
    suspend fun deleteEvents(vararg entity: EventEntity)
}
