package com.rino.self_services.model.pojo.forgetPassword

import com.google.gson.annotations.SerializedName

data class ResponseOTP(@SerializedName("success"    ) var success    : Boolean,
                       @SerializedName("message"    ) var message    : String? = null
)
