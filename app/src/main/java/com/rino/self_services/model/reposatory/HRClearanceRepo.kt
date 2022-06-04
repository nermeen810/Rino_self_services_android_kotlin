package com.rino.self_services.model.reposatory

import android.app.Application
import android.content.Context
import android.util.Log
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.AttachmentResponse
import com.rino.self_services.model.pojo.HRClearanceDetails
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.model.pojo.HRSeeAllData
import com.rino.self_services.model.pojo.hrClearance.HrClearanceResponse
import com.rino.self_services.ui.seeAllHr.HRClearanceRequest
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import okhttp3.MultipartBody
import java.io.IOException
import javax.inject.Inject

class HrClearanceRepo  @Inject constructor(private val apiDataSource: ApiDataSource, private val context: Application){
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE))
    private val sharedPreference: Preference = PreferenceDataSource(preference)

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
                        result = Result.Error(Exception("Bad Reques "))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        result = Result.Error(Exception("Not Found"))
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
    suspend fun getHRDetails(hrClearanceDetailsRequest: HRClearanceDetailsRequest):Result<HRClearanceDetails?>{
        var result: Result<HRClearanceDetails?> = Result.Loading
        try {
            val response = apiDataSource.getHRDetails("Bearer "+sharedPreference.getToken(),hrClearanceDetailsRequest.entity,hrClearanceDetailsRequest.requestID)
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
                        result = Result.Error(Exception("Bad Reques "))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                        result = Result.Error(Exception("Not Found"))
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

    suspend fun createAttachment(attachments:CreateAttachmentRequest):Result<AttachmentResponse?>{
        var result: Result<AttachmentResponse?> = Result.Loading
        var list:List<MultipartBody.Part> = listOf(attachments.parts)
        try {
            val response = apiDataSource.createAttachment("Bearer "+sharedPreference.getToken(),attachments.id,attachments.action,attachments.entity,list)
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
}


data class CreateAttachmentRequest(var action:String,var id:Int,var entity:Int,var parts:MultipartBody.Part)
data class CreateAttachmentForPaymentRequest(var action:String,var id:Int,var parts:MultipartBody.Part?)