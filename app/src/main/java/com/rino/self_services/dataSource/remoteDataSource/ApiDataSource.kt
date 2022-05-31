package com.rino.self_services.dataSource.remoteDataSource

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(grant_type:String,
                      username:String,
                      password:String,
                      client_id:String) = apiService.login(grant_type,username,password,client_id)


}