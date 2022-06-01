package com.rino.self_services.model.dataSource.remoteDataSource


import com.rino.self_services.model.pojo.LoginResponse
import com.rino.self_services.model.pojo.payment.PaymentHomeResponse
import com.rino.self_services.model.pojo.payment.SearchResponse
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

    @GET("api/requests/{me_or_others}/period/{period_value}")
    suspend fun getPaymentHomeList(@Header("Authorization"   ) auth: String,
                                   @Path("me_or_others")me_or_other :String,
                                   @Path("period_value")period_value :String):Response<PaymentHomeResponse>

    @GET("/api/requests/search/{search_txt}")
    suspend fun searchRequest(@Header("Authorization"   ) auth: String,
                                   @Path("search_txt")  search_txt :String):Response<SearchResponse>
}