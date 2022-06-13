package com.rino.self_services.model.pojo
import com.google.gson.annotations.SerializedName

data class PaymentProcessDetails(
    @SerializedName("data") var data:PaymentProcessDetailsData?)
data class PaymentProcessDetailsData(
    @SerializedName("id") var id:Int
    ,@SerializedName("date") var date:String
    ,@SerializedName("department") var department:String
    ,@SerializedName("provision") var provision:String
    ,@SerializedName("desc") var desc:String
    ,@SerializedName("amount") var amount:Double?
    ,@SerializedName("limit") var limit:Double?
    ,@SerializedName("paymentmethod") var paymentMethod:String
    ,@SerializedName("balance") var balance:Double?
    ,@SerializedName("paytype") var payType:String?
    ,@SerializedName("beneficiary") var beneficiary:String
    ,@SerializedName("status_code") var statusCode:String
    ,@SerializedName("status") var status:String
    ,@SerializedName("status_date") var statusDate: String
    ,@SerializedName("status_by") var statusBy:String
    ,@SerializedName("step") var step:Int
    ,@SerializedName("current") var current: Current
    ,@SerializedName("action") var action:Action
    ,@SerializedName("notes") var notes:String?
    ,@SerializedName("attachments") var attachments:List<Attachment>?
    ,@SerializedName("hasPermission") var hasPermission:Boolean?
    ,@SerializedName("hasMadeAction") var hasMadeAction:Boolean?
)
