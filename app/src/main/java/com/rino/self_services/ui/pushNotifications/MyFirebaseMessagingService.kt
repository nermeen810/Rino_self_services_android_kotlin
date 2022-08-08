package com.rino.self_services.ui.pushNotifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
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
    private val NOTIFICATION_ID = 100
    private lateinit var navToPPDetails: NavToDetails
    private lateinit var navToHRDetails: HRClearanceDetailsRequest
    private lateinit var preference: MySharedPreference
    private lateinit var pendingIntent: PendingIntent
    private  lateinit var DetailsIntnet:Intent

    var pp_or_hr = ""

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


//            if (remoteMessage.notification != null) {
//                showNotification(
//                    applicationContext,
//                    remoteMessage.notification?.title,
//                    remoteMessage.notification?.body
//                )
//            }
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
        val ii: Intent = Intent(context, MainActivity::class.java)
        ii.data = Uri.parse("custom://" + System.currentTimeMillis())
        ii.action = "actionstring" + System.currentTimeMillis()
//        val args = Bundle()
//        var pi = PendingIntent.getActivity(
//            applicationContext,
//            0,
//            ii,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
        preference = MySharedPreference(
            context.getSharedPreferences(
                PREF_FILE_NAME,
                MODE_PRIVATE
            )
        )
        val sharedPreference: Preference = PreferenceDataSource(preference)

        if (sharedPreference.isLogin()) {
            if (pp_or_hr == "hr") {

//                args.putParcelable("hr_clearance_details", navToHRDetails)
//                args.putParcelable("nav_to_see_all_clearance", NavSeeAll("", "", ""))
//                pendingIntent = NavDeepLinkBuilder(context)
//                    .setGraph(R.navigation.nav_graph)
//                    .setDestination(R.id.HRClearanceDetailsFragment)
//                    .setArguments(args)
//                    .createPendingIntent()
            } else if (pp_or_hr == "pp") {

                DetailsIntnet = Intent(this,MainActivity::class.java)
                DetailsIntnet.putExtra("detailsData",navToPPDetails)
                pendingIntent = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(DetailsIntnet)
                    getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }
                /*
                val resultIntent = Intent(this, ResultActivity::class.java)
// Create the TaskStackBuilder
val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
    // Add the intent, which inflates the back stack
    addNextIntentWithParentStack(resultIntent)
    // Get the PendingIntent containing the entire back stack
    getPendingIntent(0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}
                 */
//                pendingIntent = Intent()
//                args.putParcelable("nav_to_pp_details", navToPPDetails)
//                args.putParcelable("nav_to_see_all_payment", NavSeeAll("", "", ""))
//                pendingIntent = NavDeepLinkBuilder(context)
//                    .setComponentName(MainActivity::class.java)
//                    .setGraph(R.navigation.nav_graph)
//                    .setDestination(R.id.homeFragment)
//                    .setArguments(args)
//                    .createPendingIntent()
            }
//            } else {
//                pendingIntent = PendingIntent.getActivity(
//                    applicationContext,
//                    0,
//                    ii,
//                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                )
//            }
        }
//        } else {
//            pendingIntent = PendingIntent.getActivity(
//                applicationContext,
//                0,
//                ii,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        }

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
            notificationManager.notify(NOTIFICATION_ID, notification)
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
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.notification else R.mipmap.ic_launcher
    }

}