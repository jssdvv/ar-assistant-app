package com.jssdvv.ara.core.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.nextOccurrence
import com.jssdvv.ara.schedule.domain.type.RecurrenceUnit
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@HiltWorker
class EventWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val description = inputData.getString(KEY_DESCRIPTION)
        val eventId = inputData.getLong(KEY_EVENT_ID, -1L)
            .takeIf { it >= 0 } ?: return Result.failure()
        val scheduleNext = inputData.getBoolean(KEY_RECURRENT, false)
        val quantity = inputData.getInt(KEY_QUANTITY, 1)
        val unitName = inputData.getString(KEY_RECURRENCE_UNIT) ?: return Result.failure()
        val dateStr = inputData.getString(KEY_DATE) ?: return Result.failure()

        showNotification(title, description, eventId)

        if (scheduleNext) {
            val currentDate = LocalDate.parse(dateStr)
            val unit = RecurrenceUnit.valueOf(unitName)
            val nextDate = currentDate.nextOccurrence(quantity, unit)

            if (nextDate != null) {
                scheduleNext(
                    eventId = eventId,
                    title = title,
                    description = description,
                    nextDate = nextDate,
                    quantity = quantity,
                    unit = unit
                )
            }
        }

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, description: String?, eventId: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_filled)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(eventId.toInt(), notification)
    }

    private fun scheduleNext(
        eventId: Long,
        title: String,
        description: String?,
        nextDate: LocalDate,
        quantity: Int,
        unit: RecurrenceUnit,
    ) {
        val nextMillis = nextDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val delay = nextMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val nextWork = OneTimeWorkRequestBuilder<EventWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    KEY_EVENT_ID to eventId,
                    KEY_TITLE to title,
                    KEY_DESCRIPTION to description,
                    KEY_DATE to nextDate.toString(),
                    KEY_RECURRENT to true,
                    KEY_QUANTITY to quantity,
                    KEY_RECURRENCE_UNIT to unit.name
                )
            )
            .addTag(eventId.toString())
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                eventId.toString(),
                ExistingWorkPolicy.REPLACE,
                nextWork
            )
    }

    companion object {
        const val CHANNEL_ID = "events_channel"
        const val KEY_EVENT_ID = "event_id"
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_DATE = "date"
        const val KEY_RECURRENT = "recurrent"
        const val KEY_QUANTITY = "quantity"
        const val KEY_RECURRENCE_UNIT = "recurrence_unit"
    }
}