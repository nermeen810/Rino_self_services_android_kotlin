package com.rino.self_services.model.pojo.forgetPassword

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(@SerializedName("email"        ) var email        : String? = null,
                                @SerializedName("otp"          ) var otp          : String? = null ,
                                @SerializedName("password"  ) var newPassword  : String? = null)