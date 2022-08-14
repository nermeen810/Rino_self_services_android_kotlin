package com.rino.self_services.model.pojo.managementAlerts

import com.google.gson.annotations.SerializedName

data class ManagementAlertsSearchResponse(  @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
                                            @SerializedName("count"   ) var count   : Int?            = null,
                                            @SerializedName("success" ) var success : Boolean?        = null,
                                            @SerializedName("message" ) var message : String?         = null)
