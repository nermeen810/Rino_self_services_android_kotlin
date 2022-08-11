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
import com.rino.self_services.model.pojo.login.ChangePasswordRequest
import com.rino.self_services.model.pojo.login.RefreshTokenResponse
import com.rino.self_services.model.pojo.notifications.DeviceTokenRequest
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
                        result = Result.Error(Exception("حدث خطأ برجاء اعادة تسجيل الدخول"))
                        sharedPreference.logout()
                        Log.i("refreshToken refresh token:", "Result $result")

                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                    }

                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاءاعادة المحاولة"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاءاعادة المحاولة"))
                    }
                    504 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاءاعادة المحاولة"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                    }
                }
            }

        } catch (e: IOException) {
            val message: String
            if (e is SocketTimeoutException) {
                message = "حدث خطأ برجاء المحاولة مرة أخرى."
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
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
                                    result = Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة "))
                                }
                                is Result.Error -> {
                                    result = Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
                    }
                    502 -> {
                        Log.e("Error 502", "Server Error")
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى "))
                    }
                    504 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء المحاولة مرة أخرى "))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                        result = Result.Error(Exception("Error"))
                    }
                }
            }

        } catch (e: IOException) {
            val message: String
            if (e is SocketTimeoutException) {
                message = "حدث خطأ برجاء المحاولة مرة أخرى."
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
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
                        result = Result.Error(Exception("خطأ فى البريد الكترونى او كلمة المرور "))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        //  result = Result.Error(Exception("Not Found"))
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
                    }
                    504 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء المحاولة مرة أخرى"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                        result = Result.Error(Exception("Error"))
                    }
                }
            }

        } catch (e: IOException) {
            val message: String
            if (e is SocketTimeoutException) {
                message = "حدث خطأ برجاء المحاولة مرة أخرى."
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
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
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
                    }
                    502 -> {
                    Log.e("Error 502", "Server Error")
                    result =
                        Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
                    }
                    504 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
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
                message = "حدث خطأ برجاء المحاولة مرة أخرى."
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
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
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))

                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال  برجاء المحاولة مرة أخرى"))
                    }
                    504 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
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
                message = "حدث خطأ برجاء المحاولة مرة أخرى."
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Result<Any?> {
        var result: Result<Any?> = Result.Loading
        try {
            val response = apiDataSource.changePassword("Bearer "+sharedPreference.getToken(),changePasswordRequest)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                Log.i("ModelRepository", "Error${response.errorBody()?.string()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Bad Request"))
                    }
                    408-> {
                        Log.e("Error 408", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة"))
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
                                    result = Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة "))
                                }
                                is Result.Error -> {
                                    result = Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء المحاولة مرة أخرى"))
                    }
                    504 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة"))
                    }
                }
            }
        }catch(e: IOException) {
            val message: String
            if (e is SocketTimeoutException) {
                message = "حدث خطأ برجاء اعادة المحاولة"
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }
    suspend fun setDeviceToken(deviceTokenRequest: DeviceTokenRequest): Result<Any?> {
        var result: Result<Any?> = Result.Loading
        try {
            val response = apiDataSource.setDeviceToken("Bearer "+sharedPreference.getToken(),deviceTokenRequest)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("setDeviceToken", "Result $result")
            } else {
                Log.i("setDeviceToken", "Error${response.errorBody()?.string()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("كلمة المرور الحالية خاطئة"))
                    }

                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("Server is down"))

                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء المحاولة مرة أخرى"))
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
                message = "حدث خطأ برجاء المحاولة مرة أخرى."
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