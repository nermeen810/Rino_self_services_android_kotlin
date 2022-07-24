package com.rino.self_services.model.dataSource.remoteDataSource



import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.model.pojo.HRClearanceDetails
import com.rino.self_services.model.pojo.PaymentProcessDetails
import com.rino.self_services.model.pojo.complaints.ComplaintResponse
import com.rino.self_services.model.pojo.forgetPassword.RequestOTP
import com.rino.self_services.model.pojo.forgetPassword.ResetPasswordRequest
import com.rino.self_services.model.pojo.hrClearance.SearchRequest
import com.rino.self_services.model.pojo.login.ChangePasswordRequest
import com.rino.self_services.model.pojo.login.PermissionResponse
import com.rino.self_services.model.pojo.payment.EditAmountRequest
import com.rino.self_services.model.pojo.profile.ProfileResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.*

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(
        grant_type: String,
        username: String,
        password: String,
        client_id: String
    ) = apiService.login(grant_type, username, password, client_id)

    suspend fun refreshToken(
        grant_type:String,
        refresh_token:String,
        client_id:String) = apiService.refreshToken(grant_type,refresh_token,client_id)
    suspend fun changePassword( auth: String, changePasswordRequest: ChangePasswordRequest)  = apiService.changePassword(auth,changePasswordRequest)

    suspend fun getPermissions(token:String) = apiService.getPermissions(token)


    suspend fun getAllRecords(token:String,future:String,me:String,from:String,to:String,page:Long)
                             = apiService.getAllRecords(token, future,me,from, to, page)
    suspend fun getAllHRRecords(
        token: String,
        meOrOthers:String,
        from: String,
        to: String,
        page: Int
    ) = apiService.getAllHRRecords(token,meOrOthers,from,to,page)

    suspend fun createAttachment(token:String,id:Int,requestType:Int,parts:List<MultipartBody.Part>?): Response<ArrayList<Attachment>> {
        val idBody: RequestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val requestType: RequestBody = requestType.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        return  apiService.createAttachments(token,idBody,requestType,parts)
    }
    suspend fun paymentAction(token: String,id: Int,action:String) = apiService.paymentAction(token,id,action)
    suspend fun clearanceAction(token: String,enity: Int,id: Int,action: String) = apiService.clearanceAction(token,enity,id, action)

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

    suspend fun searchHrRequest( auth: String, search_txt :SearchRequest) = apiService.searchHrRequest(auth,search_txt)

    suspend fun requestOTP(requestOTP: RequestOTP) = apiService.requestOTP(requestOTP)
    suspend fun resetPassword(resetPasswrdRequest: ResetPasswordRequest)= apiService.resetPassword(resetPasswrdRequest)

    suspend fun getNotificationsCount(auth: String) = apiService.getNotificationsCount(auth)
    suspend fun getAllNotifications(auth: String) = apiService.getAllNotifications(auth)
    suspend fun setNotificationAsRead(auth: String,notification_id :Int)  = apiService.setNotificationAsRead(auth,notification_id)


    suspend fun getDepartmentList(auth: String) = apiService.getDepartmentList(auth)


    suspend fun createComplaints( auth: String
                                 , department:String
                                 , officer:String
                                 , body:String
                                 , attachments: List<MultipartBody.Part>?):Response<ComplaintResponse?>{
        val departmentBody: RequestBody = department.toRequestBody("text/plain".toMediaTypeOrNull())
        val officerBody: RequestBody = officer.toRequestBody("text/plain".toMediaTypeOrNull())
        val bodyBody: RequestBody = body.toRequestBody("text/plain".toMediaTypeOrNull())

        return apiService.createComplaints(auth,departmentBody,officerBody,bodyBody,attachments)

    }

    suspend fun getComplaintsList(auth: String) = apiService.getComplaintsList(auth)



    suspend fun getProfileData( auth: String) = apiService.getProfileData(auth)


    suspend fun editAmount(auth: String,
                           id :Int,
                          editAmountRequest: EditAmountRequest) = apiService.editAmount(auth,id,editAmountRequest)

    suspend fun getAmountChangelog(auth: String,id :Int) = apiService.getAmountChangelog(auth,id)

}