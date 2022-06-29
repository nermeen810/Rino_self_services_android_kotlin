package com.rino.self_services.model.pojo.profile

import com.google.gson.annotations.SerializedName

  data class ProfileResponse (
    @SerializedName("arabicName"       ) var arabicName       : String? = null,
    @SerializedName("email"            ) var email            : String? = null,
    @SerializedName("phoneNumber"      ) var phoneNumber      : String? = null,
    @SerializedName("departmentArabic" ) var departmentArabic : String? = null
)