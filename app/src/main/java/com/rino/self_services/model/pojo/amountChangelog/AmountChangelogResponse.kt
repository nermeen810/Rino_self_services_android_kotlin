package com.rino.self_services.model.pojo.amountChangelog

import com.google.gson.annotations.SerializedName

data class AmountChangelogResponse(
    @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf(),
    @SerializedName("status"  ) var status  : Boolean?        = null,
    @SerializedName("message" ) var message : String?         = null
)

data class Data (
    @SerializedName("id"             ) var id             : Int?    = null,
    @SerializedName("requestId"      ) var requestId      : Int?    = null,
    @SerializedName("oldAmount"      ) var oldAmount      : Int,
    @SerializedName("newAmount"      ) var newAmount      : Int,
    @SerializedName("createdAt"      ) var createdAt      : String? = null,
    @SerializedName("employeeEmail"  ) var employeeEmail  : String? = null,
    @SerializedName("employeeName"   ) var employeeName   : String? = null,
    @SerializedName("employeeId"     ) var employeeId     : Int?    = null,
    @SerializedName("departmentName" ) var departmentName : String? = null
)
