package com.rino.self_services.model.pojo

import com.google.gson.annotations.SerializedName

data class HRSeeAllData(
    @SerializedName("data"    ) var data    : ArrayList<HRSeeAllItem> = arrayListOf(),
    @SerializedName("total"   ) var total   : Int?            = null,
    @SerializedName("success" ) var success : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null
)

data class HRSeeAllItem(

    @SerializedName("id"          ) var id         : Int?     = null,
    @SerializedName("date"        ) var date       : String?  = null,
    @SerializedName("department"  ) var department : String?  = null,
    @SerializedName("status_code" ) var statusCode : String?  = null,
    @SerializedName("status"      ) var status     : String?  = null,
    @SerializedName("step"        ) var step       : Int?     = null,
    @SerializedName("status_date" ) var statusDate : String?  = null,
    @SerializedName("status_by"   ) var statusBy   : String?  = null,
    @SerializedName("action"      ) var action     : HRSeeAllAction ,
    @SerializedName("current"     ) var current    : HRSeeAllCurrent,
    @SerializedName("start"       ) var start      : String?  = null,
    @SerializedName("end"         ) var end        : String?  = null,
    @SerializedName("employee"    ) var employee   : String?  = null,
    @SerializedName("code"        ) var code       : String?  = null,
    @SerializedName("reason"      ) var reason     : String?  = null,
    @SerializedName("entity"      ) var entity     : Int?     = null,
    @SerializedName("type"        ) var type       : String?  = null)

            data class HRSeeAllAction(
    @SerializedName("name") var name:String?
    ,@SerializedName("since") var since:String?
    ,@SerializedName("cancel") var cancel:Boolean?
    ,@SerializedName("valid") var valid:Boolean?
    ,@SerializedName("users") var users:List<String>?
    )
data class HRSeeAllCurrent(
    @SerializedName("name") var name:String
    ,@SerializedName("users") var users:List<String>)