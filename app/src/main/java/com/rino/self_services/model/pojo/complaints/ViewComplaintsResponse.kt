package com.rino.self_services.model.pojo.complaints

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody


data class ComplaintResponse(
    @SerializedName("department"  ) var department  : String?                = null,
    @SerializedName("officer"     ) var officer     : String?                = null,
    @SerializedName("body"        ) var body        : String?                = null,
    @SerializedName("date"   ) var createdAt   : String?                = null,
    @SerializedName("attachments" ) var attchements : ArrayList<Attchements> = arrayListOf()
)

data class Attchements (
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("url"  ) var url  : String? = null

)

data class CreateComplaintRequest(var department:String,var officer:String,var body:String,var parts:List<MultipartBody.Part>?)
