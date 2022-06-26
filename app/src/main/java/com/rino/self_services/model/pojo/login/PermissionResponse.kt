package com.rino.self_services.model.pojo.login

import com.google.gson.annotations.SerializedName

data class PermissionResponse(
    @SerializedName("isGM"             ) var isGM             : Boolean,
    @SerializedName("isDepartmentHead" ) var isDepartmentHead : Boolean
)
