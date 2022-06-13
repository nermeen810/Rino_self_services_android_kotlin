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



@Parcelize
data class NavToAttachment(var enity:Int?,var attachments:Array<Attachment>,var isPaymentProcess:Boolean,var meOrOther:String,var id:Int,var isActionBefore:Boolean):Parcelable