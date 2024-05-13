package com.stancefreak.monkob.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.stancefreak.monkob.R
import com.stancefreak.monkob.views.navigation.NavigationActivity
import java.util.Date

class MessagingServices: FirebaseMessagingService() {

    val NOTIF_NAME = "sfreak"

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("tes data fcm", message.notification?.channelId.toString())
        if (message.notification != null) {
            message.notification?.let {
                showNotification(
                    applicationContext,
                    it.title!!,
                    it.body!!,
                    it.channelId!!.toInt()
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun showNotification(
        context: Context,
        title: String,
        message: String,
        notifId: Int
    ) {
        val icon = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.ic_logo_temp
        )
        val i = Intent(context, NavigationActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val pi = PendingIntent.getActivity(
            context,
            0,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notif: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notif = NotificationCompat.Builder(context, NOTIF_NAME)
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_logo_temp)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setContentIntent(pi)
                .setSound(uri)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setWhen(System.currentTimeMillis())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).setContentText(message).build()

            val notifManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notifChannel = NotificationChannel(
                NOTIF_NAME,
                message,
                NotificationManager.IMPORTANCE_HIGH
            )

            notifManager.apply {
                createNotificationChannel(notifChannel)
                notify(notifId, notif)
            }
        } else {
            notif = NotificationCompat.Builder(context)
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_logo_temp)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).setContentText(message).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(notifId, notif)
        }
    }

}