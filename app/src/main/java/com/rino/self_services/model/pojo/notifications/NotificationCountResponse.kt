package com.rino.self_services.model.pojo.notifications

import com.google.gson.annotations.SerializedName

data class NotificationCountResponse(
    @SerializedName("data") var data: DataCount?,
    @SerializedName("success") var success: Boolean,
    @SerializedName("message") var message: String? = null
)
data class DataCount(
    @SerializedName("paymentNotifies") var payments:Int?,
    @SerializedName("clearanceNotifies") var hr:Int?,
    @SerializedName("orderNotifies") var managementAlerts:Int?
)
