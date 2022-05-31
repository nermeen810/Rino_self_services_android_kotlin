package com.rino.self_services.model.dataSource.remoteDataSource


import com.rino.self_services.model.pojo.LoginResponse
import com.rino.self_services.model.pojo.SeeAllPaymentProcessResponse
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

    @Headers( "Content-Type: application/json;charset=UTF-8")
    @GET("{future}/{me}/from/{from}/to/{to}/page/{page}")
    suspend fun getAllRecords(@Header("Authorization") token:String,@Path("future") future:String,@Path("me") me:String ,@Path("from") from:String,@Path("to") to:String,@Path("page") page:Int): Response<SeeAllPaymentProcessResponse>
}