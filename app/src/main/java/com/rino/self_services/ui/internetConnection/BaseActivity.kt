package com.rino.self_services.ui.internetConnection

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), MyConnectivityReceiver.ConnectivityReceiverListener {
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(MyConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }


    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {
            val messageToUser = "حدث خطأ في الاتصال بالانترنت برجاء فحص لشبكة"
            mSnackBar = Snackbar.make(findViewById(R.id.rootLayout), messageToUser, Snackbar.LENGTH_LONG) //Assume "rootLayout" as the root layout of every activity.
            mSnackBar?.duration = Snackbar.LENGTH_LONG
            mSnackBar?.show()
        } else {
            if(mSnackBar?.isShown == true) {
                val messageToUser = "انترنت متصل الان"
                mSnackBar = Snackbar.make(
                    findViewById(R.id.rootLayout),
                    messageToUser,
                    Snackbar.LENGTH_LONG
                ) //Assume "rootLayout" as the root layout of every activity.
                mSnackBar?.duration = Snackbar.LENGTH_LONG
                mSnackBar?.show()
            }
        }


    }

    override fun onResume() {
        super.onResume()

        MyConnectivityReceiver.connectivityReceiverListener = this
    }
    override  fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}