package com.rino.self_services.model.reposatory

import android.app.Application
import android.content.Context
import android.util.Log
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.HRSeeAllData
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import java.io.IOException
import javax.inject.Inject

class HRClearanceRepo@Inject constructor(private val apiDataSource: ApiDataSource, private val context: Application){
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE))

    private val sharedPreference: Preference = PreferenceDataSource(preference)
    suspend fun getHRClearanceAllRecords(hrClearanceRequest: HRClearanceRequest): Result<HRSeeAllData?> {
        var result: Result<HRSeeAllData?> = Result.Loading
        try {
            val response = apiDataSource.getAllHRRecords("Bearer "+sharedPreference.getToken(),hrClearanceRequest.meOrOthers,hrClearanceRequest.from,hrClearanceRequest.to,hrClearanceRequest.pageNumber)
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
}
data class HRClearanceRequest(
    var from:String
    ,var to:String
    , var meOrOthers:String
    ,var pageNumber:Int)

