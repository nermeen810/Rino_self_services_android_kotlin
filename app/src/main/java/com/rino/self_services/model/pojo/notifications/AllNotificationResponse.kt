package com.rino.self_services.model.pojo.notifications

import com.google.gson.annotations.SerializedName

data class AllNotificationResponse (
    @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
    @SerializedName("success" ) var success : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null
)


data class Data (

    @SerializedName("id"          ) var id          : Int?     = null,
    @SerializedName("requestid"   ) var requestid   : Int?     = null,
    @SerializedName("date"        ) var date        : String?  = null,
    @SerializedName("category"    ) var category    : String?  = null,
    @SerializedName("subcategory" ) var subcategory : String?  = null,
    @SerializedName("body"        ) var body        : String?  = null,
    @SerializedName("isread"      ) var isread      : Boolean? = null

)
