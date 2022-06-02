package com.rino.self_services.model.pojo

import com.google.gson.annotations.SerializedName

data class HRSeeAllData(
    @SerializedName("data") var date: List<HRSeeAllItem>
,@SerializedName("total") var totalPages:Int)

data class HRSeeAllItem(
    @SerializedName("id") var id:Int
,@SerializedName("data") var date:String
,@SerializedName("department") var department:String
,@SerializedName("status_code") var statusCode:String
,@SerializedName("status") var status:String
,@SerializedName("step") var step:Int
,@SerializedName("action") var action:HRSeeAllAction
,@SerializedName("paymentmethod") var paymentmethod:String?
,@SerializedName("balance") var balance:Double?
,@SerializedName("beneficiary") var beneficiary:String?
,@SerializedName("status_date") var statusDate:String?
,@SerializedName("status_by") var statusBy:String?
,@SerializedName("start") var start:String?
,@SerializedName("end") var end:String?
,@SerializedName("code") var code:String
,@SerializedName("reason") var reason:String?
,@SerializedName("type") var type:String?
,@SerializedName("entity") var entity:Int?
,@SerializedName("employee") var employee:String
,@SerializedName("current") var current:HRSeeAllCurrent?)
data class HRSeeAllAction(
    @SerializedName("name") var name:String?
    ,@SerializedName("since") var since:String?
    ,@SerializedName("cancel") var cancel:Boolean?
    ,@SerializedName("valid") var valid:Boolean?
    ,@SerializedName("users") var users:List<String>?
    )
data class HRSeeAllCurrent(
    @SerializedName("name") var name:String
    ,@SerializedName("users") var users:List<String>)