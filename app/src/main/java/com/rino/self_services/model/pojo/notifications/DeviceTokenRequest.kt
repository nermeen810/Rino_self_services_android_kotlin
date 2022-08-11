package com.rino.self_services.model.pojo.notifications

import com.google.gson.annotations.SerializedName

data class DeviceTokenRequest(
    @SerializedName("token") var token: String,
    @SerializedName("deviceid") var deviceId: String
)
