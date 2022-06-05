package com.rino.self_services.model.pojo.notifications

import com.google.gson.annotations.SerializedName

data class NotificationCountResponse (@SerializedName("data"    ) var data          : Int,
                                      @SerializedName("success"    ) var success    : Boolean,
                                      @SerializedName("message"    ) var message    : String? = null
)