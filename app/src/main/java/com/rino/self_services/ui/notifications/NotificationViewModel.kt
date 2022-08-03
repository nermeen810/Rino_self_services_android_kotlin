package com.rino.self_services.ui.notifications

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rino.self_services.model.pojo.notifications.AllNotificationResponse
import com.rino.self_services.model.pojo.notifications.SetNotificationAsRead
import com.rino.self_services.model.reposatory.NotificationRepo
import com.rino.self_services.utils.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val modelRepository:NotificationRepo) : ViewModel(){
    private var _getAllNotification = MutableLiveData<AllNotificationResponse?>()
    private var _setNotificationASRead = MutableLiveData<SetNotificationAsRead?>()
    private var _readNotificationPosition = MutableLiveData<Int>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    private var _setError = MutableLiveData<String>()
    private var _noData = MutableLiveData<Boolean>()

    val loading: LiveData<Int>
        get() = _loading

    val setError: LiveData<String>
        get() = _setError

    val noData: LiveData<Boolean>
        get() = _noData

    val getAllNotification: LiveData<AllNotificationResponse?>
        get() = _getAllNotification

    val setNotificationASRead: LiveData<SetNotificationAsRead?>
        get() = _setNotificationASRead

    val readNotificationPosition: LiveData<Int>
        get() = _readNotificationPosition

    fun getAllNotification(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.getAllNotification()) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    Log.i("getAllNotification:", "${result.data}")
                    _getAllNotification.postValue(result.data)
                }

                is Result.Error -> {
                    Log.e("getAllNotification:", "${result.exception.message}")
                    _setError.postValue(result.exception.message)


                }
                is Result.Loading -> {
                    Log.i("getNotificationCount", "Loading")
                }
            }
        }
    }

    fun setNotificationAsRead(pos:Int , id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.setNotificationAsRead(id)) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    Log.i("setNotificationAsRead:", "${result.data}")
                    _setNotificationASRead.postValue(result.data)
                    _readNotificationPosition.postValue(pos)
                }

                is Result.Error -> {
                    Log.e("setNotificationAsRead:", "${result.exception.message}")
                    _setError.postValue(result.exception.message)

                }
                is Result.Loading -> {
                    Log.i("setNotificationAsRead", "Loading")
                }
            }
        }
    }
}