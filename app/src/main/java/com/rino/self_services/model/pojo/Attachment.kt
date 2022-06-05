package com.rino.self_services.model.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attachment(
    @SerializedName("name") var name:String
    ,@SerializedName("url") var url:String
    ,@SerializedName("userName") var userName:String?
    ,@SerializedName("jobTitle") var jobTitle:String?
    ,@SerializedName("date") var date: String?): Parcelable