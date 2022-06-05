package com.rino.self_services.model.reposatory

import android.app.Application
import android.content.Context
import android.util.Log
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.dataSource.remoteDataSource.ApiDataSource
import com.rino.self_services.model.pojo.LoginRequest
import com.rino.self_services.model.pojo.LoginResponse
import com.rino.self_services.model.pojo.notifications.AllNotificationResponse
import com.rino.self_services.model.pojo.notifications.NotificationCountResponse
import com.rino.self_services.model.pojo.notifications.SetNotificationAsRead
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import java.io.IOException
import javax.inject.Inject

class NotificationRepo @Inject constructor(private val apiDataSource: ApiDataSource,private val context: Application)
{
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE))

    private val sharedPreference: Preference = PreferenceDataSource(preference)

    suspend fun getNotificationCount(): Result<NotificationCountResponse?> {
        var result: Result<NotificationCountResponse?> = Result.Loading
        try {
            val response = apiDataSource.getNotificationsCount("Bearer "+sharedPreference.getToken())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getNotificationCount", "Result $result")
            } else {
                Log.i("getNotificationCount", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
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

    suspend fun getAllNotification(): Result<AllNotificationResponse?> {
        var result: Result<AllNotificationResponse?> = Result.Loading
        try {
            val response = apiDataSource.getAllNotifications("Bearer "+sharedPreference.getToken())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getANotificationCount", "Result $result")
            } else {
                Log.i("getANotificationCount", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
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

    suspend fun setNotificationAsRead(notification_id :Int): Result<SetNotificationAsRead?> {
        var result: Result<SetNotificationAsRead?> = Result.Loading
        try {
            val response = apiDataSource.setNotificationAsRead("Bearer "+sharedPreference.getToken(),notification_id)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("getANotificationCount", "Result $result")
            } else {
                Log.i("getANotificationCount", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
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


}