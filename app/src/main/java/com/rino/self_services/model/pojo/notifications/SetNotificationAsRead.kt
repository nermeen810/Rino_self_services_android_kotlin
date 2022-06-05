package com.rino.self_services.model.pojo.notifications

import com.google.gson.annotations.SerializedName

data class SetNotificationAsRead(
    @SerializedName("data"    ) var data    : Data?    = Data(),
    @SerializedName("success" ) var success : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null
)
