package com.rino.self_services.model.reposatory



import android.util.Log
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource

import android.app.Application
import android.content.Context
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource

import com.rino.self_services.model.pojo.*
//import com.rino.self_services.model.pojo.AttachmentResponse

import com.rino.self_services.model.pojo.CreateAttachmentForPaymentRequest
import com.rino.self_services.model.pojo.login.RefreshTokenResponse

import com.rino.self_services.model.pojo.payment.PaymentHomeResponse
import com.rino.self_services.model.pojo.payment.SearchResponse
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import java.io.IOException
import javax.inject.Inject




class PaymentRepo @Inject constructor(private val apiDataSource: ApiDataSource,private val context: Application){
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE))

    private val sharedPreference: Preference = PreferenceDataSource(preference)

    private suspend fun refreshToken(): Result<RefreshTokenResponse?> {
        var result: Result<RefreshTokenResponse?> = Result.Loading

        try {
            val response = apiDataSource.refreshToken("refresh_token",sharedPreference.getRefreshToken(),Constants.client_id)
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
                        result = Result.Error(Exception("حدث حطأ برجاء اعادة تسجيل الدخول"))
                        logout()
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
                            Result.Error(Exception("حدث خطأ أثناء الاتصال بالانترنت برجاء فحص الشبكة"))
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

    fun logout() {
        sharedPreference.logout()
    }
    fun isLogin():Boolean{
        return sharedPreference.isLogin()
    }
    suspend fun getPaymentHomeList(me_or_other: String,
                              period_value: String): Result<PaymentHomeResponse?> {
        var result: Result<PaymentHomeResponse?> = Result.Loading
        try {
            val response = apiDataSource.getPaymentHomeList("Bearer "+sharedPreference.getToken(), me_or_other, period_value)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                Log.i("ModelRepository", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Bad Request "))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                          result = Result.Error(Exception("Not Found"))
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
                                    result = Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة "))
                    }
                    else -> {
                        Log.e("Error", response.code().toString())
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


    suspend fun searchRequest(searchTxt:String): Result<SearchResponse?> {
        var result: Result<SearchResponse?> = Result.Loading
        try {
            val response = apiDataSource.searchRequest("Bearer "+sharedPreference.getToken(),searchTxt)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("searchRequest", "Result $result")
            } else {
                Log.i("searchRequest", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Bad Reques "))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        result = Result.Error(Exception("Not Found"))
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
                                    result = Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة "))
                    }
                    else -> {
                        Log.e("Error", response.code().toString())
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
        suspend fun getAllRecords(seeAllRequest: SeeAllRequest): Result<SeeAllPaymentProcessResponse?> {
        var result: Result<SeeAllPaymentProcessResponse?> = Result.Loading
        try {
            val response = apiDataSource.getAllRecords(seeAllRequest.token,seeAllRequest.currentFutuer,seeAllRequest.me,seeAllRequest.from,seeAllRequest.to,seeAllRequest.page)
            Log.d("ayman status Code",response.code().toString())
            if (response.isSuccessful) {
                result = Result.Success(response.body())

            } else {
                when (response.code()) {
                    500 -> {
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة "))
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
                                    result = Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                }
            }
        }catch(e: IOException) {
            result = Result.Error(e)
        }
        return result
    }
    suspend fun getPaymentDetails(token:String,id:Int): Result<PaymentProcessDetails?> {
        var result: Result<PaymentProcessDetails?> = Result.Loading
        try {
            val response = apiDataSource.getPaymentDetails(token,id)
            if (response.isSuccessful) {
                result = Result.Success(response.body())

            } else {
                when (response.code()) {
                    500 -> {
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة "))
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
                                    result = Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                }
            }
        }catch(e: IOException) {
            result = Result.Error(e)}
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
                            result = Result.Error(Exception("Bad Reques "))
                        }
                        404 -> {
                            Log.e("Error 404", "Not Found")
                            result = Result.Error(Exception("Not Found"))
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
                                        result = Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                    }
                                }
                            }
                            else {
                                result =
                                    Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                            }
                        }
                        500 -> {
                            Log.e("Error 500", "Server Error")
                            result = Result.Error(Exception("server is down"))
                        }
                        502 -> {
                            Log.e("Error 502", "Time out")
                            result =
                                Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة "))
                        }
                        else -> {
                            Log.e("Error", response.code().toString())
                            result = Result.Error(Exception("Error"))
                        }
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
    suspend fun paymentAction(id:Int,action:String): Result<ActionResponse?> {
        var result: Result<ActionResponse?> = Result.Loading
        try {
            val response = apiDataSource.paymentAction("Bearer "+sharedPreference.getToken(), action = action, id = id)
            if (response.isSuccessful) {
                result = Result.Success(response.body())

            } else {
                when (response.code()) {
                    500 -> {
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        result =
                            Result.Error(Exception("حدث حطأ برجاء اعادة المحاولة "))
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
                                    result = Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                                }
                            }
                        }
                        else {
                            result =
                                Result.Error(Exception("حدث حطأ برجاء تسجيل الخروج ثم اعادة تسجيل الدخول"))
                        }
                    }
                }
            }
        }catch(e: IOException) {
            result = Result.Error(e)}
        return result
    }
}