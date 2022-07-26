package com.rino.self_services.ui.pushNotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rino.self_services.R
import com.rino.self_services.ui.main.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var NOTIFICATION_CHANNEL_ID = "net.larntech.notification"
    val NOTIFICATION_ID = 100

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
       // super.onMessageReceived(remoteMessage)
        Log.e("message","Message Received ...")
        println("messsssssssssssage received ")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("NotificationData", "Message data payload: " + remoteMessage.getData());
            val processType = remoteMessage.data["processType"]
            val id = remoteMessage.data["id"]
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]

            if(processType=="clearance"){
                val entityType = remoteMessage.data["entityType"]

            }
            else if(processType == "payment")
            {

            }

            showNotification(applicationContext, title, body)
        }
        else {
            if (remoteMessage.notification != null) {
                showNotification(
                    applicationContext,
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )
            }
        }
//        if (remoteMessage.data.size > 0) {
//            val title = remoteMessage.data["title"]
//            val body = remoteMessage.data["body"]
//            showNotification(applicationContext, title, body)
//        } else {
//            val title = remoteMessage.notification!!.title
//            val body = remoteMessage.notification!!.body
//            showNotification(applicationContext, title, body)
//        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
      //  sendRegistrationToServer(token);

        Log.e("token","New Token :"+token)
    }

    private fun sendRegistrationToServer(token: String) {
           Log.d("ServiceFirebase", "Refreshed token: $token - send this to server?")
            FirebaseMessaging.getInstance().subscribeToTopic("global")

    }
    private fun showNotification(
        context: Context,
        title: String?,
        message: String?
    ) {
        val ii: Intent
        ii = Intent(context, MainActivity::class.java)
        ii.data = Uri.parse("custom://" + System.currentTimeMillis())
        ii.action = "actionstring" + System.currentTimeMillis()


        val pi =
            PendingIntent.getActivity(
                applicationContext,
                0,
                ii,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.e("Notification", "Created in up to orio OS device");
            notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(getNotificationIcon())
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setWhen(System.currentTimeMillis())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                title,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notification = NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setAutoCancel(true)
                .setContentText(message)
                .setContentIntent(pi)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.notification else R.mipmap.ic_launcher
    }

}
