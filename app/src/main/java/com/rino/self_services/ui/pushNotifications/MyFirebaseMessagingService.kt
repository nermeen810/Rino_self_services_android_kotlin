package com.rino.self_services.ui.pushNotifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rino.self_services.R
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.ui.main.MainActivity
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import com.rino.self_services.utils.PREF_FILE_NAME

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var NOTIFICATION_CHANNEL_ID = "net.larntech.notification"
    private var NOTIFICATION_ID = 0
    private lateinit var navToPPDetails: NavToDetails
    private lateinit var navToHRDetails: HRClearanceDetailsRequest
    private lateinit var preference: MySharedPreference
    private lateinit var pendingIntent: PendingIntent
    private  lateinit var DetailsIntnet:Intent
    private var notificationID = 0
    var pp_or_hr = ""

//   override fun zzm(intent: Intent) {
//        Log.i("uniqbadge", "zzm")
//        val keys = intent.extras!!.keySet()
//        for (key in keys) {
//            try {
//                Log.i("uniq", " " + key + " " + intent.extras!![key])
//                if (key == "badge") {
//                    val cnt = intent.extras!![key].toString()
//                    val badgeCount = Integer.valueOf(cnt)
//                    Log.i("uniq", " badge count $badgeCount")
////                    ShortcutBadger.applyCount(this, badgeCount)
//                    Log.i("uniq", " " + "end")
//                }
//            } catch (e: Exception) {
//                Log.i("uniqbadge", "zzm Custom_FirebaseMessagingService" + e.message)
//            }
//        }
//        super.zzm(intent)
//    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("message", "Message Received ...")
        println("messsssssssssssage received ")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("NotificationData", "Message data payload: " + remoteMessage.getData());
            val processType = remoteMessage.data["processType"]
            val id = remoteMessage.data["id"]
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            notificationID = remoteMessage.data["notificationId"]?.toInt() ?: 0

            if (processType == "clearance") {
                pp_or_hr = "hr"
                val entityType = remoteMessage.data["entityType"]
                navToHRDetails = HRClearanceDetailsRequest(
                    entityType?.toInt() ?: -1,
                    id?.toInt() ?: -1,
                    true,
                    "me"
                )

            } else if (processType == "payment") {
                pp_or_hr = "pp"
                navToPPDetails = NavToDetails("me", id?.toInt() ?: -1, true)
            }

            showNotification(applicationContext, title, body)
        } else {
            if (remoteMessage.data.size > 0) {
                val title = remoteMessage.data["title"]
                val body = remoteMessage.data["body"]
                showNotification(applicationContext, title, body)
            } else {
                val title = remoteMessage.notification!!.title
                val body = remoteMessage.notification!!.body
                showNotification(applicationContext, title, body)
            }



        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //  sendRegistrationToServer(token);

        Log.e("token", "New Token :" + token)
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
        preference = MySharedPreference(
            context.getSharedPreferences(
                PREF_FILE_NAME,
                MODE_PRIVATE
            )
        )
        val sharedPreference: Preference = PreferenceDataSource(preference)

        if (sharedPreference.isLogin()) {
            if (pp_or_hr == "hr") {
                DetailsIntnet = Intent(this,MainActivity::class.java)
                DetailsIntnet.putExtra("notificationID",notificationID)
                DetailsIntnet.putExtra("hrDetailsData",navToHRDetails)
                pendingIntent = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(DetailsIntnet)
                    getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }

            } else if (pp_or_hr == "pp") {

                DetailsIntnet = Intent(this,MainActivity::class.java)
                DetailsIntnet.putExtra("detailsData",navToPPDetails)
                DetailsIntnet.putExtra("notificationID",notificationID)
                pendingIntent = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(DetailsIntnet)
                    getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }

            }
        }

        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.e("Notification", "Created in up to orio OS device");
            notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(getNotificationIcon())
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
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
            notificationManager.notify(NOTIFICATION_ID++, notification)
        } else {
            notification = NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setAutoCancel(true)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID++, notification)
        }
    }

    private fun getNotificationIcon(): Int {
        return R.mipmap.ic_launcher
    }

}