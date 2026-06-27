package com.jssdvv.ara.schedule.domain.usecase

import com.jssdvv.ara.schedule.domain.model.Event
import com.jssdvv.ara.schedule.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

data class EventsDataManager(
    val select: SelectEvents,
    val upsert: UpsertEvents,
    val delete: DeleteEvents,
)

class SelectEvents(private val repository: EventRepository) {
    operator fun invoke(): Flow<List<Event>> =
        repository.selectAllEvents()
}

class UpsertEvents(private val repository: EventRepository) {
    suspend operator fun invoke(model: Event) = repository.upsertEvent(model)
}

class DeleteEvents(private val repository: EventRepository) {
    suspend operator fun invoke(vararg model: Event) = repository.deleteEvent(*model)
}
