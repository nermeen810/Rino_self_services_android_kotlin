package com.rino.self_services.model.pojo.complaints

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody


data class ComplaintResponse(
    @SerializedName("department"  ) var department  : String?               = null,
    @SerializedName("officer"     ) var officer     : String?                = null,
    @SerializedName("body"        ) var body        : String?                = null,
    @SerializedName("date"        ) var createdAt   : String?                = null,
    @SerializedName("attachments" ) var attchements : ArrayList<Attachments> = arrayListOf()
)

data class ComplaintItemResponse(
    @SerializedName("id"          ) var id :Int ,
    @SerializedName("department"  ) var department  : String                ,
    @SerializedName("officer"     ) var officer     : String                ,
    @SerializedName("body"        ) var body        : String                ,
    @SerializedName("createdAt"   ) var createdAt   : String               ,
    @SerializedName("attchements" ) var attchements : ArrayList<Attachments> = arrayListOf()
)
@Parcelize
data class Attachments(
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("url"  ) var url  : String? = null

) :Parcelable

data class CreateComplaintRequest(var department:String,var officer:String,var body:String,var parts:List<MultipartBody.Part>?)
