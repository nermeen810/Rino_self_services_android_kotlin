package com.rino.self_services.model.reposatory


import android.util.Log
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.PaymentProcessDetails
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
                            Result.Error(Exception("time out"))
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