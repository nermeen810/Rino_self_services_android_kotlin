package com.rino.self_services.model.dataSource.remoteDataSource



import com.rino.self_services.model.pojo.*

import com.rino.self_services.model.pojo.LoginResponse

import com.rino.self_services.model.pojo.PaymentProcessDetails
import com.rino.self_services.model.pojo.SeeAllPaymentProcessResponse
//import com.rino.self_services.model.pojo.hrClearance.ActionApproveOrDeny
import com.rino.self_services.model.pojo.hrClearance.HrClearanceResponse


import com.rino.self_services.model.pojo.payment.PaymentHomeResponse
import com.rino.self_services.model.pojo.payment.SearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("connect/token")
    suspend fun login(@Field("grant_type") grant_type:String,
                      @Field("username") username:String,
                      @Field("password") password:String,
                      @Field("client_id") client_id:String,
    ): Response<LoginResponse>


    @GET("api/{future}/{me}/from/{from}/to/{to}/page/{page}")
    suspend fun getAllRecords(@Header("Authorization") token:String,@Path("future") future:String,@Path("me") me:String ,@Path("from") from:String,@Path("to") to:String,@Path("page") page:Long): Response<SeeAllPaymentProcessResponse>

    @GET("api/requests/{ID}/details")
    suspend fun getPaymentDetails(@Header("Authorization")token:String,@Path("ID") id:Int): Response<PaymentProcessDetails>
    @GET("api/clearancerequests/{meOrOthers}/from/{from}/to/{to}/page/{pageNumber}")
    suspend fun getAllHRRecords(@Header("Authorization") token:String, @Path("meOrOthers") meOrOthers:String, @Path("from") from:String, @Path("to") to:String, @Path("pageNumber") pageNumber: Int):Response<HRSeeAllData>
    @GET("api/clearancerequests/{enity}/{requestID}/details")
    suspend fun getHRDetails(@Header("Authorization")token:String,@Path("enity") enity:Int,@Path("requestID") requestID:Int):Response<HRClearanceDetails>


    @GET("api/requests/{me_or_others}/period/{period_value}")
    suspend fun getPaymentHomeList(@Header("Authorization"   ) auth: String,
                                   @Path("me_or_others")me_or_other :String,
                                   @Path("period_value")period_value :String):Response<PaymentHomeResponse>

    @GET("/api/requests/search/{search_txt}")
    suspend fun searchRequest(@Header("Authorization"   ) auth: String,
                                   @Path("search_txt")  search_txt :String):Response<SearchResponse>

    @GET("api/clearancerequests/{me_or_others}/period/{period_value}")
    suspend fun getHrClearanceHomeList(@Header("Authorization"   ) auth: String,
                                   @Path("me_or_others")me_or_other :String,
                                   @Path("period_value")period_value :String):Response<HrClearanceResponse>


    @Multipart
    @POST("api/clearancerequests/action/")
    suspend fun createAttachments(@Header("Authorization") auth: String,@Part("id") id:RequestBody,@Part("Entity") Entity:RequestBody, @Part Attachments: List<MultipartBody.Part>?,@Part("Notes") notes:RequestBody):Response<HRClearanceDetails>

//    @POST("api/clearancerequests/action/{entity}/{id}/{action}")
//    suspend fun approveRequest(@Header("Authorization"   ) auth: String,
//                                       @Path("entity")entity :Int?,
//                                       @Path("id")id :Int?,
//                                      @Path("action")action :String):Response<ActionApproveOrDeny>


    @Multipart
    @POST("api/requests/action/")
    suspend fun createAttachmentsForPayment(@Header("Authorization") auth: String,@Part("id") id:RequestBody,@Part Attachments: List<MultipartBody.Part>?,@Part("Notes") notes:RequestBody):Response<PaymentProcessDetails>
}

