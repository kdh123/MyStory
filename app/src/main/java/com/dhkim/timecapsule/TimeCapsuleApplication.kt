package com.dhkim.timecapsule

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TimeCapsuleApplication : Application() {

    companion object {
        const val CHANNEL_ID = "timeCapsule"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel("timeCapsule", "Main Timecapsule")
    }

    private fun createNotificationChannel(channelName: String, desc: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = desc
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}