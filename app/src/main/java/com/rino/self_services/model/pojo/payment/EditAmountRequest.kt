package com.rino.self_services.model.pojo.payment

import com.google.gson.annotations.SerializedName

class EditAmountRequest {
    @SerializedName("newAmount" ) var newAmount : Int?  = null

}