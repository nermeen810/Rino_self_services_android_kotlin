package com.rino.self_services.model.pojo.hrClearance

import com.google.gson.annotations.SerializedName


data class SearchHrResponse(@SerializedName("data"    ) var data    : ArrayList<Items> = arrayListOf(),
                            @SerializedName("total"   ) var total   : Int?            = null,
                            @SerializedName("success" ) var success : Boolean?        = null,
                            @SerializedName("message" ) var message : String?         = null)
