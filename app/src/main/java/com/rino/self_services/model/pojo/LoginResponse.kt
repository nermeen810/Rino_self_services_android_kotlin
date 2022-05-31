package com.rino.self_services.model.pojo

import com.google.gson.annotations.SerializedName

data class LoginResponse(@SerializedName("access_token"        ) var token        : String,
                         @SerializedName("expires_in"       ) var expires_in       : Int,
                         @SerializedName("token_type"       ) var token_type       : String,
                         @SerializedName("refreshToken" ) var refreshToken : String,
                         @SerializedName("scope"       ) var scope       : String)
