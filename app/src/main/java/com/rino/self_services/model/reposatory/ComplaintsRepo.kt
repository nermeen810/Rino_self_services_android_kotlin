package com.rino.self_services.model.reposatory

import android.app.Application
import android.content.Context
import android.util.Log
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.model.pojo.CreateAttachmentRequest
import com.rino.self_services.model.pojo.complaints.ComplaintItemResponse
import com.rino.self_services.model.pojo.complaints.ComplaintResponse
import com.rino.self_services.model.pojo.complaints.CreateComplaintRequest
import com.rino.self_services.model.pojo.login.PermissionResponse
import com.rino.self_services.model.pojo.login.RefreshTokenResponse
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ComplaintsRepo  @Inject constructor(private val apiDataSource: ApiDataSource, private val context: Application)
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
                    408 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة"))
                    }

                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة"))
                    }
                    504 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                    }
                }
            }

        }catch(e: IOException) {
            val message: String
            if (e is SocketTimeoutException) {
                message = "حدث خطأ برجاء اعادة المحاولة"
                result = Result.Error(java.lang.Exception(message))
            } else {
                result = Result.Error(e)
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }

    suspend fun getPermission(): Result<PermissionResponse?> {
        var result: Result<PermissionResponse?> = Result.Loading
        try {
            val response = apiDataSource.getPermissions("Bearer "+sharedPreference.getToken())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getPermission", "Result $result")
            } else {
                Log.i("getPermission", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Bad Request"))
                    }
                    408-> {
                        Log.e("Error 408", "Time out")
                        result =
                            Result.Error(Exception("Time out"))
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
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("server is down"))
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
                result = Result.Error(e)
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }

    suspend fun getDepartmentList(): Result<ArrayList<String>?> {
        var result: Result<ArrayList<String>?> = Result.Loading
        try {
            val response = apiDataSource.getDepartmentList("Bearer "+sharedPreference.getToken())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getDepartmentList", "Result $result")
            } else {
                Log.i("getDepartmentList", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Bad Request"))
                    }
                    408-> {
                        Log.e("Error 408", "Time out")
                        result =
                            Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة"))
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
                        result = Result.Error(Exception("حدث خطأ فى السرفر برجاء اعادة المحاولة"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث خطأ فى السرفر برجاء اعادة المحاولة"))
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
                result = Result.Error(e)
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }

    suspend fun getComplaintsList(): Result<ArrayList<ComplaintItemResponse>?> {
        var result: Result<ArrayList<ComplaintItemResponse>?> = Result.Loading
        try {
            val response = apiDataSource.getComplaintsList("Bearer "+sharedPreference.getToken())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getComplaintsList", "Result $result")
            } else {
                Log.i("getComplaintsList", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Bad Request"))
                    }
                    408 -> {
                        Log.e("Error 408", "Time out")
                        result =
                            Result.Error(Exception("Time out"))
                    }
                    401 -> {
                        Log.e("Error 401", "Not Auth please, logout and login again")
                        if (sharedPreference.isLogin()) {
                            Log.i(
                                "Model Repo:",
                                "isLogin:" + sharedPreference.isLogin() + ", token:" + sharedPreference.getToken() + ",  refresh token:" + sharedPreference.getRefreshToken()
                            )
                            val res = refreshToken()
                            when (res) {
                                is Result.Success -> {
                                    result =
                                        Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة "))
                                }
                                is Result.Error -> {
                                    result =
                                        Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        } else {
                            result =
                                Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("server is down"))
                    }
                    504 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("Time out"))
                    }
                }
            }
    }catch(e: IOException) {
    val message: String
    if (e is SocketTimeoutException) {
        message = "Time out"
        result = Result.Error(java.lang.Exception(message))
    } else {
        result = Result.Error(e)
        Log.e("ModelRepository", "IOException ${e.message}")
        Log.e("ModelRepository", "IOException ${e.localizedMessage}")
    }
}
    return result
    }

    suspend fun createComplaint(createComplaintRequest: CreateComplaintRequest):Result<ComplaintResponse?>{
        var result: Result<ComplaintResponse?> = Result.Loading

        try {
            val response = createComplaintRequest.parts?.let {
                apiDataSource.createComplaints(
                    "Bearer " + sharedPreference.getToken(),
                    createComplaintRequest.department,
                    createComplaintRequest.officer,
                    createComplaintRequest.body,
                    it
                )
            }
            if (response != null) {
                if (response.isSuccessful) {
                    result = Result.Success(response.body())
                    Log.i("createComplaint", "Result $result")
                } else {
                    Log.i("createComplaint", "Error${response.errorBody()}")
                    when (response.code()) {
                        400 -> {
                            Log.e("Error 400", "Bad Request")
                            result = Result.Error(Exception("Bad Request"))
                        }
                        408 -> {
                            Log.e("Error 408", "Time out")
                            result =
                                Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة"))
                        }
                        401 -> {
                            Log.e("Error 401", "Not Auth please, logout and login again")
                            if (sharedPreference.isLogin()) {
                                Log.i(
                                    "Model Repo:",
                                    "isLogin:" + sharedPreference.isLogin() + ", token:" + sharedPreference.getToken() + ",  refresh token:" + sharedPreference.getRefreshToken()
                                )
                                val res = refreshToken()
                                when (res) {
                                    is Result.Success -> {
                                        result =
                                            Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة "))
                                    }
                                    is Result.Error -> {
                                        result =
                                            Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                    }
                                }
                            } else {
                                result =
                                    Result.Error(Exception("حدث خطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                            }
                        }
                        500 -> {
                            Log.e("Error 500", "Server Error")
                            result =
                                Result.Error(Exception("حدث خطأ فى السرفر برجاء اعادة المحاولة"))
                        }
                        502 -> {
                            result =
                                Result.Error(Exception("حدث خطأ فى السرفر برجاء اعادة المحاولة"))
                        }
                        504 -> {
                            Log.e("Error 504", "Time out")
                            result =
                                Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة"))
                        }
                    }
                }
            }
            }catch(e: IOException) {
                val message: String
                if (e is SocketTimeoutException) {
                    message = "حدث خطأ برجاء اعادة المحاولة"
                    result = Result.Error(java.lang.Exception(message))
                } else {
                    result = Result.Error(e)
                    Log.e("ModelRepository", "IOException ${e.message}")
                    Log.e("ModelRepository", "IOException ${e.localizedMessage}")
                }
            }
            return result
    }

}