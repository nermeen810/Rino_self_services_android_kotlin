package com.rino.self_services.model.pojo
import com.google.gson.annotations.SerializedName

data class SeeAllPaymentProcessResponse(@SerializedName("data") var data:List<Item> ,@SerializedName("total") var totalPages:Int)
data class Item(@SerializedName("id")var id:Int?,
                @SerializedName("amount") var amount:Double?
                , @SerializedName("beneficiary") var beneficiary:String?
                , @SerializedName("date") var date:String
                , @SerializedName("department") var department:String
                , @SerializedName("status_code") var statusCode:String
                , @SerializedName("status")var status:String
                , @SerializedName("step")var step:Int
                , @SerializedName("status_date") var statusDate:String
                , @SerializedName("action") var action:Action
                , @SerializedName("current") var current:Current
                ,@SerializedName("balance") var balance:Double?)
{}
data class Current(@SerializedName("name") var name: String,@SerializedName("users") var users: List<String>){}
data class Action(@SerializedName("name") var name:String?, @SerializedName("since") var since:String?, @SerializedName("cancel") var cancel:Boolean, @SerializedName("valid") var valid:Boolean, @SerializedName("users") var users:List<String>?)