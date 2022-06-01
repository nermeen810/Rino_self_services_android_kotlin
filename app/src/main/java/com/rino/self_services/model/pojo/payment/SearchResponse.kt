package com.rino.self_services.model.pojo.payment

import com.google.gson.annotations.SerializedName

data class SearchResponse  ( @SerializedName("data"    ) var data    : ArrayList<Items> = arrayListOf(),
                             @SerializedName("total"   ) var total   : Int?            = null,
                             @SerializedName("success" ) var success : Boolean?        = null,
                             @SerializedName("message" ) var message : String?         = null

)
//data class SearchData (
//
//    @SerializedName("id"            ) var id            : Int?     = null,
//    @SerializedName("date"          ) var date          : String?  = null,
//    @SerializedName("department"    ) var department    : String?  = null,
//    @SerializedName("status_code"   ) var statusCode    : String?  = null,
//    @SerializedName("status"        ) var status        : String?  = null,
//    @SerializedName("step"          ) var step          : Int?     = null,
//    @SerializedName("status_date"   ) var statusDate    : String?  = null,
//    @SerializedName("status_by"     ) var statusBy      : String?  = null,
//    @SerializedName("action"        ) var action        : Action?  = Action(),
//    @SerializedName("current"       ) var current       : Current? = Current(),
//    @SerializedName("paymentmethod" ) var paymentmethod : String?  = null,
//    @SerializedName("balance"       ) var balance       : String?  = null,
//    @SerializedName("amount"        ) var amount        : Int?     = null,
//    @SerializedName("beneficiary"   ) var beneficiary   : String?  = null
//
//)