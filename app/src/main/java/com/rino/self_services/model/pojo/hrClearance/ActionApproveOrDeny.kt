package com.rino.self_services.model.pojo.hrClearance

import com.google.gson.annotations.SerializedName

data class ActionApproveOrDeny(
    @SerializedName("data"    ) var data    : ActionData?    = ActionData(),
    @SerializedName("success" ) var success : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null
)

data class ActionData (

    @SerializedName("id"          ) var id         : Int?     = null,
    @SerializedName("action"      ) var action     : Action?  = Action(),
    @SerializedName("status_code" ) var statusCode : String?  = null,
    @SerializedName("status"      ) var status     : String?  = null,
    @SerializedName("status_date" ) var statusDate : String?  = null,
    @SerializedName("status_by"   ) var statusBy   : String?  = null,
    @SerializedName("current"     ) var current    : Current? = Current(),
    @SerializedName("step"        ) var step       : Int?     = null

)

