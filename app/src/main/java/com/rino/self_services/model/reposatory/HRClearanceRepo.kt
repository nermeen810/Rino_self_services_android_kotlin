package com.rino.self_services.model.reposatory

import android.app.Application
import android.content.Context
import android.util.Log
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.*
import com.rino.self_services.model.pojo.hrClearance.HrClearanceResponse
import com.rino.self_services.model.pojo.hrClearance.SearchHrResponse
import com.rino.self_services.model.pojo.hrClearance.SearchRequest
import com.rino.self_services.model.pojo.login.RefreshTokenResponse
import com.rino.self_services.model.pojo.payment.SearchResponse
import com.rino.self_services.ui.seeAllHr.HRClearanceRequest
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class HrClearanceRepo  @Inject constructor(private val apiDataSource: ApiDataSource, private val context: Application){
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
                        result = Result.Error(Exception("Bad Request "))
                    }
                    408 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة "))
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                    }
                    502 ->{
                        Log.e("Error 502", "Server Error")
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة "))
                    }
                    504 -> {
                        Log.e("Error 504", "Time out")
                        result =
                            Result.Error(Exception("حدث خطأ برجاء اعادة المحاولة "))
                    }
                    else -> {
                        Log.e("Error", response.code().toString())
                        result = Result.Error(Exception("Error"))
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


    suspend fun getHrClearanceHomeList(me_or_other: String,
                                   period_value: String): Result<HrClearanceResponse?> {
        var result: Result<HrClearanceResponse?> = Result.Loading
        try {
            val response = apiDataSource.getHrClearanceHomeList("Bearer "+sharedPreference.getToken(), me_or_other, period_value)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getHrClearanceHomeList", "Result $result")
            } else {
                Log.i("getHrClearanceHomeList", "Error${response.errorBody()}")
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
                result = Result.Error(Exception("server is down"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }
    suspend fun searchRequest(searchTxt:String): Result<SearchHrResponse?> {
        var result: Result<SearchHrResponse?> = Result.Loading
        try {
            val response = apiDataSource.searchHrRequest("Bearer "+sharedPreference.getToken(),
                SearchRequest(searchTxt))
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("searchHrRequest", "Result $result")
            } else {
                Log.i("searchHrRequest", "Error${response.errorBody()}")
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
                result = Result.Error(Exception("server is down"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }
    suspend fun getHRDetails(hrClearanceDetailsRequest: HRClearanceDetailsRequest):Result<HRClearanceDetails?>{
        var result: Result<HRClearanceDetails?> = Result.Loading
        try {
            val response = apiDataSource.getHRDetails("Bearer "+sharedPreference.getToken(),hrClearanceDetailsRequest.entity,hrClearanceDetailsRequest.requestID)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getHRDetails", "Result $result")
            } else {
                Log.i("getHRDetails", "Error${response.errorBody()}")
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
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
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
                result = Result.Error(Exception("server is down"))
                Log.e("ModelRepository", "IOException ${e.message}")
                Log.e("ModelRepository", "IOException ${e.localizedMessage}")
            }
        }
        return result
    }
    suspend fun getHRClearanceAllRecords(hrClearanceRequest: HRClearanceRequest):Result<HRSeeAllData?>{
        var result: Result<HRSeeAllData?> = Result.Loading
        try {
            val response = apiDataSource.getAllHRRecords("Bearer "+sharedPreference.getToken(),hrClearanceRequest.meOrOthers,hrClearanceRequest.from,hrClearanceRequest.to,hrClearanceRequest.pageNumber)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getHrClearanceHomeList", "Result $result")
            } else {
                Log.i("getHrClearanceHomeList", "Error${response.errorBody()}")
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
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
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

    suspend fun createAttachment(attachments: CreateAttachmentRequest):Result<ArrayList<Attachment>?>{
        var result: Result<ArrayList<Attachment>?> = Result.Loading

        try {
            val response = attachments.parts?.let {
                apiDataSource.createAttachment("Bearer "+sharedPreference.getToken(),attachments.id,attachments.attachmentType,
                    it
                )
            }
            if (response != null) {
                if (response.isSuccessful) {
                    result = Result.Success(response.body())
                    Log.i("getHrClearanceHomeList", "Result $result")
                } else {
                    Log.i("getHrClearanceHomeList", "Error${response.errorBody()}")
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
                                Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                        }
                        502 -> {
                            result =
                                Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
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
                    result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                    Log.e("ModelRepository", "IOException ${e.message}")
                    Log.e("ModelRepository", "IOException ${e.localizedMessage}")
                }
            }
            return result
    }
    suspend fun clearanceAction(id:Int,action:String,entity:Int): Result<ActionResponse?> {
        var result: Result<ActionResponse?> = Result.Loading
        try {
            val response = apiDataSource.clearanceAction("Bearer "+sharedPreference.getToken(), action = action, id = id, enity = entity)
            if (response.isSuccessful) {
                result = Result.Success(response.body())

            } else {
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
                        result = Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالسرفر برجاء اعادة المحاولة"))
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
}