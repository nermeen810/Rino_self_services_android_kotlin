package com.rino.self_services.model.pojo.payment

import com.google.gson.annotations.SerializedName

data class EditAmountRequest (
    @SerializedName("newAmount" ) var newAmount : Int?  = null)
