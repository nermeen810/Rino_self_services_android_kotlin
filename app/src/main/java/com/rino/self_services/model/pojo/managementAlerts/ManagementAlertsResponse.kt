package com.rino.self_services.model.pojo.managementAlerts

import com.google.gson.annotations.SerializedName

data class ManagementAlertsResponse (
    @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
    @SerializedName("count"   ) var count   : Int?            = null,
    @SerializedName("success" ) var success : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null)

data class Data (

    @SerializedName("title" ) var title : String?          = null,
    @SerializedName("from"  ) var from  : String?          = null,
    @SerializedName("to"    ) var to    : String?          = null,
    @SerializedName("items" ) var items : ArrayList<Items> = arrayListOf(),
    @SerializedName("count" ) var count : Int?             = null

)


data class Items (

    @SerializedName("id"          ) var id         : Int,
    @SerializedName("status"      ) var status     : Int,
    @SerializedName("status_date" ) var statusDate : String,
    @SerializedName("custom_data" ) var customData : CustomData? = CustomData()

)

data class CustomData (

    @SerializedName("details"     ) var details     : String? = null,
    @SerializedName("amount"      ) var amount      : Float?    = null,
    @SerializedName("beneficiary" ) var beneficiary : String? = null

)