package com.rino.self_services.model.reposatory

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.login.LoginRequest
import com.rino.self_services.model.pojo.login.LoginResponse
import com.rino.self_services.model.pojo.forgetPassword.RequestOTP
import com.rino.self_services.model.pojo.forgetPassword.ResetPasswordRequest
import com.rino.self_services.model.pojo.forgetPassword.ResponseOTP
import com.rino.self_services.model.pojo.login.PermissionResponse
import com.rino.self_services.model.pojo.login.RefreshTokenResponse
import com.rino.self_services.model.pojo.profile.ProfileResponse
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.PREF_FILE_NAME
import java.io.IOException
import javax.inject.Inject
import com.rino.self_services.utils.Result
import java.net.SocketTimeoutException

class UserRepo @Inject constructor(private val apiDataSource: ApiDataSource,private  val context: Application)
{
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE))

    private val sharedPreference: Preference = PreferenceDataSource(preference)

    private suspend fun refreshToken(): Result<RefreshTokenResponse?> {
        var result: Result<RefreshTokenResponse?> = Result.Loading

        try {
            val response = apiDataSource.refreshToken("refresh_token",sharedPreference.getRefreshToken(),
                Constants.client_id)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("refreshToken", "Result $result")
                sharedPreference.setToken(response.body()?.accessToken!!)
                sharedPreference.setRefreshToken(response.body()?.refreshToken!!)
            } else {
                Log.i("refreshToken", "Error${response.message()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("?????? ?????? ?????????? ?????????? ?????????? ????????????"))
                        sharedPreference.logout()
                        Log.i("refreshToken refresh token:", "Result $result")

                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                    }

                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("?????? ?????? ?????????? ?????????????? ?????????????????? ?????????? ?????? ????????????"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
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

    suspend fun getProfileData(): Result<ProfileResponse?> {
        var result: Result<ProfileResponse?> = Result.Loading
        try {
            val response = apiDataSource.getProfileData("Bearer "+sharedPreference.getToken())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getProfileData", "Result $result")
            } else {
                Log.i("getProfileData", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        //  result = Result.Error(Exception("Not Found"))
                    }
                    401 ->{
                        Log.e("Error 401", "Not Auth please, logout and login again")
                        if (sharedPreference.isLogin()) {
                            Log.i(
                                "Model Repo:",
                                "isLogin:" + sharedPreference.isLogin() + ", token:" + sharedPreference.getToken() + ",  refresh token:" + sharedPreference.getRefreshToken()
                            )
                            val res = refreshToken()
                            when(res) {
                                is Result.Success -> {
                                    result = Result.Error(Exception("?????? ?????? ?????????? ?????????? ???????????????? "))
                                }
                                is Result.Error -> {
                                    result = Result.Error(Exception("?????? ?????? ?????????? ?????????? ???????????? ???? ?????????? ?????????? ????????????"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("?????? ?????? ?????????? ?????????? ???????????? ???? ?????????? ?????????? ????????????"))
                        }
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("?????? ?????? ?????????? ?????????? ???????????????? "))
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

     suspend fun requestOTP(requestOTP: RequestOTP): Result<ResponseOTP?> {
        var result: Result<ResponseOTP?> = Result.Loading
        try {
            val response = apiDataSource.requestOTP(requestOTP)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                Log.i("ModelRepository", "Error${response.errorBody()?.string()}")
                val gson = Gson()
                val eventResponse =
                    gson.fromJson(response.errorBody()?.string(), ResponseOTP::class.java)
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("???????? ???????????????? ????????"))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        result = Result.Error(Exception("???????????????? ???????? ?????? ???????????? ???????????????????? ?????? ??????????"))
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server idd down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("?????? ?????? ?????????? ?????????????? ?????????????????? ?????????? ???????????????? ?????? ????????"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                        //      result = Result.Error(Exception("Error"))
                    }
                }
            }

        } catch (e: IOException) {
            val message: String
            if (e is SocketTimeoutException) {
                message = "?????? ?????? ?????????? ?????????????? ?????????????????? ?????????? ???????????????? ?????? ????????."
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(e)
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }

     suspend fun resetPassword(resetPasswrdRequest: ResetPasswordRequest): Result<ResponseOTP?> {
        var result: Result<ResponseOTP?> = Result.Loading
        try {
            val response = apiDataSource.resetPassword(resetPasswrdRequest)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                val gson = Gson()
                val eventResponse =
                    gson.fromJson(response.errorBody()?.string(), ResponseOTP::class.java)
                Log.i("ModelRepository", "Error${response.errorBody()?.string()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception(eventResponse.message))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        result = Result.Error(Exception(eventResponse.message))
                    }

                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("Server is down"))

                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("?????? ?????? ?????????? ?????????????? ?????????????????? ?????????? ???????????????? ?????? ????????"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                        //      result = Result.Error(Exception("Error"))
                    }
                }
            }

        } catch (e: IOException) {
            val message: String
            if (e is SocketTimeoutException) {
                message = "?????? ?????? ?????????? ?????????????? ?????????????????? ?????????? ???????????????? ?????? ????????."
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(e)
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }

    fun logout() {
        sharedPreference.setLogin(false)
        sharedPreference.setToken("")
        sharedPreference.setRefreshToken("")

    }
}