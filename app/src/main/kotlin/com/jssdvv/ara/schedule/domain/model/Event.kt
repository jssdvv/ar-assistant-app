package com.jssdvv.ara.schedule.domain.model

import com.jssdvv.ara.schedule.domain.type.RecurrenceUnit
import java.time.LocalDate

data class Event(
    val id: Int = 0,
    val activityId: Int,
    val title: String,
    val description: String?,
    val date: LocalDate,
    val recurrent: Boolean = false,
    val quantity: Int = 0,
    val recurrenceUnit: RecurrenceUnit = RecurrenceUnit.ONCE
)