package com.rino.self_services.model.pojo.login

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

    @SerializedName("id_token"      ) var idToken      : String? = null,
    @SerializedName("access_token"  ) var accessToken  : String? = null,
    @SerializedName("expires_in"    ) var expiresIn    : Int?    = null,
    @SerializedName("token_type"    ) var tokenType    : String? = null,
    @SerializedName("refresh_token" ) var refreshToken : String? = null,
    @SerializedName("scope"         ) var scope        : String? = null
)
