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
                        result = Result.Error(Exception("بريد الكترونى خاطئ"))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        result = Result.Error(Exception("المستخدم صاحب هذا البريد الإلكتروني غير موجود"))
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server idd down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالانترنت برجاء المحاولة مرة أخرى"))
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
                message = "حدث خطأ أثناء الاتصال بالانترنت برجاء المحاولة مرة أخرى."
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
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالانترنت برجاء المحاولة مرة أخرى"))
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
                message = "حدث خطأ أثناء الاتصال بالانترنت برجاء المحاولة مرة أخرى."
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