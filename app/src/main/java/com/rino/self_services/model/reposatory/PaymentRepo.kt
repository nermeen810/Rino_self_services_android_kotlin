package com.rino.self_services.model.reposatory

import android.util.Log
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.SeeAllRequest
import com.rino.self_services.model.pojo.SeeAllPaymentProcessResponse
import com.rino.self_services.utils.Result
import java.io.IOException
import javax.inject.Inject

class PaymentRepo @Inject constructor(private val apiDataSource: ApiDataSource){
    suspend fun getAllRecords(seeAllRequest: SeeAllRequest): Result<SeeAllPaymentProcessResponse?> {
        var result: Result<SeeAllPaymentProcessResponse?> = Result.Loading
        try {

            val response = apiDataSource.getAllRecords(seeAllRequest.token,seeAllRequest.currentFutuer,seeAllRequest.me,seeAllRequest.from,seeAllRequest.to,seeAllRequest.page)
            Log.d("ayman statusCode", response.code().toString())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.d("ayman statusCode", response.code().toString())
//                Log.i("ModelRepository", "Result $result")
            } else {
                when (response.code()) {
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    502 -> {
                        Log.e("Error 502", "Time out")
                        result =
                            Result.Error(Exception("time out"))
                    }
                }
            }
        }catch(e: IOException) {
            result = Result.Error(e)
        }
        return result
    }
}