package com.rino.self_services.model.pojo.payment

import com.google.gson.annotations.SerializedName

data class EditAmountErrorResponse (
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null
        )