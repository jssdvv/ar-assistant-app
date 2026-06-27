package com.jssdvv.ara.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.jssdvv.ara.core.data.repository.NotificationService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HiltAndroidApp : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()

    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationService.CHANNEL_ID,
            "Recurrent Events",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Calendar event reminders"
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}