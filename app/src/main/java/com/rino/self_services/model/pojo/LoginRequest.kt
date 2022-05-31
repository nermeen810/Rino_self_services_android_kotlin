package com.rino.self_services.model.pojo

import com.google.gson.annotations.SerializedName

data class LoginRequest(@SerializedName("Email"    ) var email    : String,
                        @SerializedName("Password" ) var password : String)
