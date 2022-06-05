package com.rino.self_services.model.pojo.forgetPassword

import com.google.gson.annotations.SerializedName

data class RequestOTP(@SerializedName("email"    ) var email    : String? = null)