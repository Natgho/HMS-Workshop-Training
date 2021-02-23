package com.hmsworkshop_week2_demo

import android.R
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage


class HuaweiPushService : HmsMessageService() {

    private val TAG = "PushKit"
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.i(TAG, "receive token:$p0")
    }

    override fun stopService(name: Intent?): Boolean {
        Log.i(TAG, "Push Service Stopped")
        return super.stopService(name)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage != null) {
            if (remoteMessage.data.isNotEmpty()) {
                Log.i(TAG, "Message data payload: " + remoteMessage.data)
            }
            if (remoteMessage.notification != null) {
                Log.i(TAG, "Message Notification Body: " + remoteMessage.notification.body)
            }
        }


        val notificationData = remoteMessage!!.dataOfMap
        if (notificationData.isEmpty()) {
            Log.e(TAG, "onMessageReceived: notification data is empty")
            return
        }

        val icon: Int = R.mipmap.sym_def_app_icon
        val title: String = notificationData.get("title")!!
        val text: String = notificationData.get("text")!!
        val channelId: String = notificationData.get("channel_id")!!


        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notificationManager = NotificationManagerCompat.from(this)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setColor(this.resources.getColor(R.color.black))
            .build()

        notificationManager.notify(1, notification)

    }

    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)
        Log.i(TAG, "receive token:$p0")

    }
}