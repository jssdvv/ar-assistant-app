package com.jssdvv.ara.schedule.data.local.mapper

import com.jssdvv.ara.schedule.data.local.entity.EventEntity
import com.jssdvv.ara.schedule.domain.model.Event

fun Event.toEntity(): EventEntity = EventEntity(
    id = id,
    activityId = activityId,
    title = title,
    description = description,
    date = date,
    recurrent = recurrent,
    quantity = quantity,
    recurrenceUnit = recurrenceUnit
)

fun EventEntity.toDomain(): Event = Event(
    id = id,
    activityId = activityId,
    title = title,
    description = description,
    date = date,
    recurrent = recurrent,
    quantity = quantity,
    recurrenceUnit = recurrenceUnit
)