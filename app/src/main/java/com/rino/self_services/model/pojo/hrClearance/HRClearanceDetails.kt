package com.rino.self_services.model.pojo

import com.google.gson.annotations.SerializedName


data class HRClearanceDetails (@SerializedName("data") var data:HRClearanceData)
data class HRClearanceData(
    @SerializedName("id") var id:Int?
,@SerializedName("date") var date:String?
,@SerializedName("department") var department:String?
,@SerializedName("reason") var reason:String?
,@SerializedName("start") var start:String?
,@SerializedName("end") var end:String?
,@SerializedName("employee_id") var employeeID:Int?
,@SerializedName("code") var code:String?
,@SerializedName("duo_days") var duoDays:Double?
,@SerializedName("status_code") var statusCode:String?
,@SerializedName("status") var status:String?
,@SerializedName("status_date") var statusDate:String?
,@SerializedName("status_by") var statusBy:String?
,@SerializedName("entity") var entity:Int?
,@SerializedName("type") var type:String?
,@SerializedName("step") var step:Int?
,@SerializedName("current") var current:HRClearancecurrent?
,@SerializedName("action") var action: HRClearanceAction?
,@SerializedName("employee") var employee:String
,@SerializedName("attachments") var attachment: Array<Attachment>
,@SerializedName("hasPermission") var hasPermission:Boolean?
,@SerializedName("hasApprovedRequest") var hasApproved:Boolean?)
data class HRClearancecurrent(
    @SerializedName("name") var name:String?
,@SerializedName("users") var users:List<String>?)
data class HRClearanceAction(
    @SerializedName("name") var name:String?
    ,@SerializedName("since") var since:String?
    ,@SerializedName("cancel") var cancel:Boolean?
    ,@SerializedName("valid") var valid:Boolean?
    ,@SerializedName("users") var users: List<String>?
)