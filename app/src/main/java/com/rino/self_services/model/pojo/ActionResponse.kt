package com.rino.self_services.model.pojo

import com.google.gson.annotations.SerializedName

data class ActionResponse(
    @SerializedName("success") var success:Boolean?
    ,@SerializedName("message") var message:String?
)
