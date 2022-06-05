package com.rino.self_services.model.pojo

import okhttp3.MultipartBody

data class CreateAttachmentForPaymentRequest(var action:String,var id:Int,var parts:ArrayList<MultipartBody.Part?>?)