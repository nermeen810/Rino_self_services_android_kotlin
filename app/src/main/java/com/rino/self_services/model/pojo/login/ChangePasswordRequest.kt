package com.rino.self_services.model.pojo.login

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest (@SerializedName("old"        ) var oldPassword        : String? = null,
                                  @SerializedName("new"        ) var newPassword        : String? = null)