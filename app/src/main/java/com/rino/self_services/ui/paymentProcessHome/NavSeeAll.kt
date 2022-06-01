package com.rino.self_services.ui.paymentProcessHome


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
 data class NavSeeAll(val me_or_others:String,val startPeriod :String,val endPeriod :String):Parcelable
