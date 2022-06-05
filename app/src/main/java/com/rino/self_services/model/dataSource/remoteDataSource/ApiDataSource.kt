package com.rino.self_services.model.dataSource.remoteDataSource



import com.rino.self_services.model.pojo.HRClearanceDetails
import com.rino.self_services.model.pojo.PaymentProcessDetails
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(
        grant_type: String,
        username: String,
        password: String,
        client_id: String
    ) = apiService.login(grant_type, username, password, client_id)

    suspend fun getAllRecords(token:String,future:String,me:String,from:String,to:String,page:Long) = apiService.getAllRecords(token, future,me,from, to, page)
    suspend fun getAllHRRecords(
        token: String,
        meOrOthers:String,
        from: String,
        to: String,
        page: Int
    ) = apiService.getAllHRRecords(token,meOrOthers,from,to,page)

    suspend fun createAttachment(token:String,id:Int,Entity:Int,parts:List<MultipartBody.Part>?): retrofit2.Response<HRClearanceDetails> {
        val idBody: RequestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//        val actionBody: RequestBody = Action.toRequestBody("text/plain".toMediaTypeOrNull())
        val enityBody: RequestBody = Entity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val notes:RequestBody = "".toRequestBody()

        return  apiService.createAttachments(token,idBody,enityBody,parts,notes)
    }
    suspend fun createAttachmentForPayment(token:String,id:Int,parts:List<MultipartBody.Part>?): retrofit2.Response<PaymentProcessDetails> {
        val idBody: RequestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val notes:RequestBody = " ".toRequestBody()

        return  apiService.createAttachmentsForPayment(token,idBody,parts,notes)
    }

    suspend fun getPaymentDetails(token:String,id:Int) = apiService.getPaymentDetails(token,id)
    suspend fun getHRDetails(token:String, enity:Int, requestID:Int) = apiService.getHRDetails(token,enity,requestID)
    suspend fun getPaymentHomeList(
        auth: String,
        me_or_other: String,
        period_value: String
    ) = apiService.getPaymentHomeList(auth,me_or_other,period_value)

    suspend fun searchRequest(
        auth: String,
        searchTxt:String
    ) = apiService.searchRequest(auth,searchTxt)


    suspend fun getHrClearanceHomeList(
        auth: String,
        me_or_other: String,
        period_value: String
    ) = apiService.getHrClearanceHomeList(auth,me_or_other,period_value)


}