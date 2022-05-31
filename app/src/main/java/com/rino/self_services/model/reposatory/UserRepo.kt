package com.rino.self_services.model.reposatory

import android.util.Log
import com.rino.self_services.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.LoginRequest
import com.rino.self_services.model.pojo.LoginResponse
import com.rino.self_services.utils.Constants
import java.io.IOException
import javax.inject.Inject
import com.rino.self_services.utils.Result
class UserRepo@Inject constructor(private val apiDataSource: ApiDataSource)
{

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse?> {
        var result: Result<LoginResponse?> = Result.Loading
        try {
            val response = apiDataSource.login(Constants.grant_type,loginRequest.email,loginRequest.password,Constants.client_id)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                Log.i("ModelRepository", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Wrong Email or Password "))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        //  result = Result.Error(Exception("Not Found"))
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("time out"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                        result = Result.Error(Exception("Error"))
                    }
                }
            }

        } catch (e: IOException) {
            result = Result.Error(e)
            Log.e("ModelRepository", "IOException ${e.message}")
            Log.e("ModelRepository", "IOException ${e.localizedMessage}")

        }
        return result
    }
}