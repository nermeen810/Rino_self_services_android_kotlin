package com.rino.self_services.ui.viewAttatchment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(var name :String,var url:String):Parcelable
