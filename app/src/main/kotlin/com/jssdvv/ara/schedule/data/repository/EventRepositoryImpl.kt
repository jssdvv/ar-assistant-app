package com.jssdvv.ara.schedule.data.repository

import com.jssdvv.ara.schedule.data.local.dao.EventDao
import com.jssdvv.ara.schedule.data.local.entity.EventEntity
import com.jssdvv.ara.schedule.data.local.mapper.toDomain
import com.jssdvv.ara.schedule.data.local.mapper.toEntity
import com.jssdvv.ara.schedule.domain.model.Event
import com.jssdvv.ara.schedule.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepositoryImpl(
    private val dao: EventDao
) : EventRepository {
    override fun selectAllEvents(): Flow<List<Event>> =
        dao.selectAllEvents().map { it.map(EventEntity::toDomain) }

    override suspend fun upsertEvent(model: Event): Long =
        dao.upsertEvents(model.toEntity())

    override suspend fun deleteEvent(vararg model: Event) =
        dao.deleteEvents(*model.map(Event::toEntity).toTypedArray())
}
