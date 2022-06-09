package com.rino.self_services.model.pojo

import okhttp3.MultipartBody
data class CreateAttachmentRequest(var id:Int,var attachmentType:Int,var parts:List<MultipartBody.Part>?)