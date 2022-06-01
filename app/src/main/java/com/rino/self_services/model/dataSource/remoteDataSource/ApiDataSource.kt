package com.rino.self_services.model.dataSource.remoteDataSource


import retrofit2.http.GET

import retrofit2.http.Header
import retrofit2.http.Path

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(
        grant_type: String,
        username: String,
        password: String,
        client_id: String
    ) = apiService.login(grant_type, username, password, client_id)

    suspend fun getAllRecords(token:String,future:String,me:String,from:String,to:String,page:Long) = apiService.getAllRecords(token, future,me,from, to, page)
    suspend fun getPaymentDetails(token:String,id:Int) = apiService.getPaymentDetails(token,id)

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