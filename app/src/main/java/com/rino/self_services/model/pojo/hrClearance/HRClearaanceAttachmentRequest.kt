package com.rino.self_services.model.pojo

import okhttp3.MultipartBody
data class CreateAttachmentRequest(var action:String,var id:Int,var entity:Int,var parts:List<MultipartBody.Part>?)