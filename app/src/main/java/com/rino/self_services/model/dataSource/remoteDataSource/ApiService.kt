package com.rino.self_services.model.dataSource.remoteDataSource



import com.google.gson.annotations.SerializedName
import com.rino.self_services.model.pojo.*

import com.rino.self_services.model.pojo.login.LoginResponse

import com.rino.self_services.model.pojo.PaymentProcessDetails
import com.rino.self_services.model.pojo.SeeAllPaymentProcessResponse
import com.rino.self_services.model.pojo.amountChangelog.AmountChangelogResponse
import com.rino.self_services.model.pojo.complaints.ComplaintItemResponse
import com.rino.self_services.model.pojo.complaints.ComplaintResponse
import com.rino.self_services.model.pojo.forgetPassword.RequestOTP
import com.rino.self_services.model.pojo.forgetPassword.ResetPasswordRequest
import com.rino.self_services.model.pojo.forgetPassword.ResponseOTP
import com.rino.self_services.model.pojo.hrClearance.HrClearanceResponse
import com.rino.self_services.model.pojo.hrClearance.SearchHrResponse
import com.rino.self_services.model.pojo.hrClearance.SearchRequest
import com.rino.self_services.model.pojo.login.ChangePasswordRequest
import com.rino.self_services.model.pojo.login.PermissionResponse
import com.rino.self_services.model.pojo.login.RefreshTokenResponse
import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsDetailsResponse
import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsResponse
import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsSearchResponse
import com.rino.self_services.model.pojo.notifications.AllNotificationResponse
import com.rino.self_services.model.pojo.notifications.DeviceTokenRequest
import com.rino.self_services.model.pojo.notifications.NotificationCountResponse
import com.rino.self_services.model.pojo.notifications.SetNotificationAsRead
import com.rino.self_services.model.pojo.payment.EditAmountRequest


import com.rino.self_services.model.pojo.payment.PaymentHomeResponse
import com.rino.self_services.model.pojo.payment.SearchResponse
import com.rino.self_services.model.pojo.profile.ProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/notifications/mobile/add")
    suspend fun setDeviceToken(@Header("Authorization"   ) auth: String,
                               @Body deviceTokenRequest: DeviceTokenRequest
    ): Response<Any?>
    @FormUrlEncoded
    @POST("connect/token")
    suspend fun login(@Field("grant_type") grant_type:String,
                      @Field("username") username:String,
                      @Field("password") password:String,
                      @Field("client_id") client_id:String,
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("connect/token")
    suspend fun refreshToken(@Field("grant_type") grant_type:String,
                             @Field("refresh_token") refresh_token:String,
                             @Field("client_id") client_id:String,
    ): Response<RefreshTokenResponse?>

    @POST("api/identity/password/reset")
    suspend fun requestOTP(@Body requestOTP: RequestOTP): Response<ResponseOTP?>

    @POST("api/identity/password/confirm-reset")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<ResponseOTP?>

    @POST("api/identity/password/change")
    suspend fun changePassword(@Header("Authorization"   ) auth: String,
                               @Body changePasswordRequest: ChangePasswordRequest): Response<Any?>

    @GET("api/identity/permissions")
    suspend fun getPermissions(@Header("Authorization") token:String):Response<PermissionResponse?>

    @GET("api/{future}/{me}/from/{from}/to/{to}/page/{page}")
    suspend fun getAllRecords(@Header("Authorization") token:String,@Path("future") future:String,@Path("me") me:String ,@Path("from") from:String,@Path("to") to:String,@Path("page") page:Long): Response<SeeAllPaymentProcessResponse?>

    @GET("api/requests/{ID}/details")
    suspend fun getPaymentDetails(@Header("Authorization")token:String,@Path("ID") id:Int): Response<PaymentProcessDetails>
    @GET("api/clearancerequests/{meOrOthers}/from/{from}/to/{to}/page/{pageNumber}")
    suspend fun getAllHRRecords(@Header("Authorization") token:String, @Path("meOrOthers") meOrOthers:String, @Path("from") from:String, @Path("to") to:String, @Path("pageNumber") pageNumber: Int):Response<HRSeeAllData?>
    @GET("api/clearancerequests/{enity}/{requestID}/details")
    suspend fun getHRDetails(@Header("Authorization")token:String,@Path("enity") enity:Int,@Path("requestID") requestID:Int):Response<HRClearanceDetails?>


    @GET("api/requests/{me_or_others}/period/{period_value}")
    suspend fun getPaymentHomeList(@Header("Authorization"   ) auth: String,
                                   @Path("me_or_others")me_or_other :String,
                                   @Path("period_value")period_value :String):Response<PaymentHomeResponse?>

    @GET("api/requests/search/{search_txt}")
    suspend fun searchRequest(@Header("Authorization"   ) auth: String,
                                   @Path("search_txt")  search_txt :String):Response<SearchResponse?>

    @GET("api/clearancerequests/{me_or_others}/period/{period_value}")
    suspend fun getHrClearanceHomeList(@Header("Authorization"   ) auth: String,
                                   @Path("me_or_others")me_or_other :String,
                                   @Path("period_value")period_value :String):Response<HrClearanceResponse?>

    @POST("api/clearancerequests/search")
    suspend fun searchHrRequest(@Header("Authorization"   ) auth: String,
                              @Body search_txt : SearchRequest):Response<SearchHrResponse?>

    @Multipart
    @POST("api/Attachments/")
    suspend fun createAttachments(@Header("Authorization") auth: String,@Part("id") id:RequestBody,@Part("RequestType") RequestType:RequestBody ,@Part Attachments: List<MultipartBody.Part>?):Response<ArrayList<Attachment>>

    @GET("api/notifications/new/count")
    suspend fun getNotificationsCount(@Header("Authorization") auth: String):Response<NotificationCountResponse?>

    @GET("api/notifications")
    suspend fun getAllNotifications(@Header("Authorization") auth: String):Response<AllNotificationResponse?>

    @PUT("api/notifications/read/{notification_id}")
    suspend fun setNotificationAsRead(@Header("Authorization") auth: String,@Path("notification_id") notification_id :Int):Response<SetNotificationAsRead?>

    @POST("api/requests/action/{id}/{action}")
    suspend fun paymentAction(
    @Header("Authorization") auth: String
    ,@Path("id")  id:Int
    ,@Path("action") action:String
):Response<ActionResponse?>


    @POST("api/clearancerequests/action/{entity}/{id}/{action}")
    suspend fun clearanceAction(
        @Header("Authorization") auth: String
        ,@Path("entity") entity:Int
        ,@Path("id") id:Int
        ,@Path("action") action: String
    ):Response<ActionResponse?>

    @GET("api/complains/departments")
    suspend fun getDepartmentList(@Header("Authorization"   ) auth: String):Response<ArrayList<String>?>

    @Multipart
    @POST("api/complains")
    suspend fun createComplaints(@Header("Authorization") auth: String
                                 ,@Part("Department") department:RequestBody
                                 ,@Part("Officer") officer:RequestBody
                                 ,@Part("Body") body:RequestBody
                                 ,@Part Attachments: List<MultipartBody.Part>?):Response<ComplaintResponse?>


    @GET("api/complains")
    suspend fun getComplaintsList(@Header("Authorization"   ) auth: String):Response<ArrayList<ComplaintItemResponse>?>

    @GET("api/identity/profile/")
    suspend fun getProfileData(@Header("Authorization"   ) auth: String):Response<ProfileResponse?>

    @POST("api/requests/{id}/edit-amount")
    suspend fun editAmount(@Header("Authorization"   ) auth: String,
                           @Path("id" ) id :Int,
                           @Body editAmountRequest: EditAmountRequest
    ):Response<Void>

    @GET("api/requests/{id}/amount-changelog")
    suspend fun getAmountChangelog(@Header("Authorization"   ) auth: String,  @Path("id" ) id :Int):Response<AmountChangelogResponse?>


    @GET("api/orders/period/{period_value}")
    suspend fun getManagementAlertsList(@Header("Authorization"   ) auth: String,
                                       @Path("period_value")period_value :String):Response<ManagementAlertsResponse?>

    @GET("api/Orders/query/{query}/page/{page}")
    suspend fun searchMARequest(@Header("Authorization") auth: String,
                                @Path("query") search_txt : String,
                                @Path("page") page: Long):Response<ManagementAlertsSearchResponse?>

    @GET("api/Orders/{id}")
    suspend fun getDetailsMARequest(@Header("Authorization") auth: String,
                                    @Path("id") id: Int):Response<ManagementAlertsDetailsResponse?>
}


