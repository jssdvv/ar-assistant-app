package com.jssdvv.ara.schedule.domain.repository

import com.jssdvv.ara.schedule.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun selectAllEvents(): Flow<List<Event>>
    suspend fun upsertEvent(model: Event): Long
    suspend fun deleteEvent(vararg model: Event)
}
