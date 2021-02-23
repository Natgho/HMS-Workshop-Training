package com.hmsworkshop_week2_demo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                "channel_1",
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "Notifications from Channel 1"

            val channel2 = NotificationChannel(
                "channel_2",
                "Channel 2",
                NotificationManager.IMPORTANCE_LOW
            )
            channel2.description = "Notifications from Channel 1"
            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)
        }
    }

}