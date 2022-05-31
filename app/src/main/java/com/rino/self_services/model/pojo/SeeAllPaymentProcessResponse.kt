package com.rino.self_services.model.pojo
import com.google.gson.annotations.SerializedName

data class SeeAllPaymentProcessResponse(@SerializedName("data") var data:List<Item> )
data class Item(@SerializedName("id")var id:Int?, @SerializedName("balance") var balance:Int?, @SerializedName("amount") var amount:Int?, @SerializedName("beneficiary") var beneficiary:String?, @SerializedName("date") var date:String, @SerializedName("department") var department:String, @SerializedName("status_code") var statusCode:String, @SerializedName("status")var status:String, @SerializedName("step")var step:Int, @SerializedName("status_date") var statusDate:String, @SerializedName("action") var action:Actions, @SerializedName("current") var current:Current){}
data class Current(@SerializedName("name") var name: String,@SerializedName("users") var users: List<String>){}
data class Actions(@SerializedName("name") var name:String?, @SerializedName("since") var since:String?, @SerializedName("cancel") var cancel:Boolean, @SerializedName("valid") var valid:Boolean, @SerializedName("users") var users:List<String>?)
/*
   "data": [
        {
            "id": 15814,
            "date": "2020-03-10T00:00:00",
            "department": "مشاريع عملاء وحدة الاسفلت",
            "status_code": "DepartmentHead",
            "status": "تم اعتماد مدير القسم",
            "step": 2,
            "status_date": "2020-03-10T15:42:04",
            "status_by": "عبدالعالي الربيعي",
            "action": {
                "name": null,
                "since": null,
                "cancel": false,
                "valid": true,
                "users": null
            },
            "current": {
                "name": "اعتماد مدير الحسابات",
                "users": [
                    "وليد محمد محمد احمد",
                    "زياد محمد الطحاينه"
                ]
            },
            "paymentmethod": null,
            "balance": 4000,
            "amount": 4000,
            "beneficiary": "مين باهادور جودري"
        },
 */