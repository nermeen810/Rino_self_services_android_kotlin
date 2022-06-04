package com.rino.self_services.model.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HRClearanceDetailsRequest(
    var entity:Int
,var requestID:Int): Parcelable