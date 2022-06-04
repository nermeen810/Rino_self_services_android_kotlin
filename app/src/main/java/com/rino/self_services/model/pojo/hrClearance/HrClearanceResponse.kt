package com.rino.self_services.model.pojo.hrClearance

import com.google.gson.annotations.SerializedName

data class HrClearanceResponse  (@SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
                                 @SerializedName("success" ) var success : Boolean?        = null,
                                 @SerializedName("message" ) var message : String?         = null)


data class Data (

    @SerializedName("title" ) var title : String?          = null,
    @SerializedName("start" ) var start : String?          = null,
    @SerializedName("end"   ) var end   : String?          = null,
    @SerializedName("count" ) var count : Int?             = null,
    @SerializedName("items" ) var items : ArrayList<Items> = arrayListOf()

)


data class Items (

    @SerializedName("id"          ) var id         : Int?     = null,
    @SerializedName("date"        ) var date       : String,
    @SerializedName("department"  ) var department : String?  = null,
    @SerializedName("status_code" ) var statusCode : String?  = null,
    @SerializedName("status"      ) var status     : String?  = null,
    @SerializedName("step"        ) var step       : Int?     = null,
    @SerializedName("status_date" ) var statusDate : String?  = null,
    @SerializedName("status_by"   ) var statusBy   : String?  = null,
    @SerializedName("action"      ) var action     : Action?  = Action(),
    @SerializedName("current"     ) var current    : Current? = Current(),
    @SerializedName("start"       ) var start      : String?  = null,
    @SerializedName("end"         ) var end        : String?  = null,
    @SerializedName("employee"    ) var employee   : String?  = null,
    @SerializedName("code"        ) var code       : String?  = null,
    @SerializedName("reason"      ) var reason     : String?  = null,
    @SerializedName("entity"      ) var entity     : Int?     = null,
    @SerializedName("type"        ) var type       : String?  = null

)


data class Current (

    @SerializedName("name"  ) var name  : String?           = null,
    @SerializedName("users" ) var users : ArrayList<String> = arrayListOf()

)

data class Action (

    @SerializedName("name"   ) var name   : String?           = null,
    @SerializedName("since"  ) var since  : String?           = null,
    @SerializedName("cancel" ) var cancel : Boolean?          = null,
    @SerializedName("valid"  ) var valid  : Boolean?          = null,
    @SerializedName("users"  ) var users  : ArrayList<String> = arrayListOf()

)