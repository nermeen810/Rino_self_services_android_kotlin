package com.rino.self_services.model.dataSource.remoteDataSource

import retrofit2.http.GET
import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(grant_type:String,
                      username:String,
                      password:String,
                      client_id:String) = apiService.login(grant_type,username,password,client_id)

    suspend fun getAllRecords(token:String,future:String,me:String,from:String,to:String,page:Int) = apiService.getAllRecords(token, future,me,from, to, page)
}