package com.rino.self_services.model.pojo.managementAlerts

import com.google.gson.annotations.SerializedName

data class ManagementAlertsDetailsResponse (
    @SerializedName("data"    ) var data    : DetailsData?    = DetailsData(),
    @SerializedName("success" ) var success : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null
)

data class DetailsData( @SerializedName("id"             ) var id             : Int?        = null,
                        @SerializedName("status"         ) var status         : Int?        = null,
                        @SerializedName("status_date"    ) var statusDate     : String?     = null,
                        @SerializedName("custom_data"    ) var customData     : CustomData? = CustomData(),
                        @SerializedName("countAll"       ) var countAll       : Int?        = null,
                        @SerializedName("timePeriod"     ) var timePeriod     : String?     = null,
                        @SerializedName("periodCategory" ) var periodCategory : String?     = null)