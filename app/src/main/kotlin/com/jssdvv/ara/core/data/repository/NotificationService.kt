package com.jssdvv.ara.core.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jssdvv.ara.schedule.domain.model.Event
import com.jssdvv.ara.schedule.domain.type.RecurrenceUnit
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class NotificationService(
    private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "events_channel"
    }

    fun schedule(event: Event) {
        if (!event.recurrent && event.recurrenceUnit == RecurrenceUnit.ONCE) {
            scheduleOnce(event)
        } else {
            scheduleRecurrent(event)
        }
    }

    private fun scheduleOnce(event: Event) {
        val delay = calculateDelay(event.date) ?: return

        val work = OneTimeWorkRequestBuilder<EventWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(buildInputData(event, scheduleNext = false))
            .addTag(event.id.toString())
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                event.id.toString(),
                ExistingWorkPolicy.REPLACE,
                work
            )
    }

    private fun scheduleRecurrent(event: Event) {
        val delay = calculateDelay(event.date) ?: return

        val work = OneTimeWorkRequestBuilder<EventWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(buildInputData(event, scheduleNext = true))
            .addTag(event.id.toString())
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                event.id.toString(),
                ExistingWorkPolicy.REPLACE,
                work
            )
    }

    fun cancel(event: Event) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(event.id.toString())
    }

    fun reschedule(event: Event) {
        cancel(event)
        schedule(event)
    }

    private fun buildInputData(event: Event, scheduleNext: Boolean) = workDataOf(
        EventWorker.KEY_EVENT_ID to event.id,
        EventWorker.KEY_TITLE to event.title,
        EventWorker.KEY_DESCRIPTION to event.description,
        EventWorker.KEY_DATE to event.date.toString(),
        EventWorker.KEY_RECURRENT to scheduleNext,
        EventWorker.KEY_QUANTITY to event.quantity,
        EventWorker.KEY_RECURRENCE_UNIT to event.recurrenceUnit.name
    )

    private fun calculateDelay(date: LocalDate): Long? {
        val eventMillis = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val delay = eventMillis - System.currentTimeMillis()
        return if (delay > 0) delay else null
    }
}