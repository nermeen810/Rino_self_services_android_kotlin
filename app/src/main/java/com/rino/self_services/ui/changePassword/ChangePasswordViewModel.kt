package com.rino.self_services.ui.changePassword

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.login.ChangePasswordRequest
import com.rino.self_services.model.reposatory.UserRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val modelRepository: UserRepo) : ViewModel() {
    private  var _changePasswordResponse = MutableLiveData<Boolean>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val changePasswordResponse: LiveData<Boolean>
        get() = _changePasswordResponse

    fun changePassword(oldPassword:String, newPassword : String){
        Log.d("changePassword","call")
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.changePassword(ChangePasswordRequest(oldPassword,newPassword))) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    result.data.let {
                            _changePasswordResponse.postValue(true)
                    }
                }
                is Result.Error -> {
                    Log.e(" changePasswordError:", "${result.exception.message}")
                    _loading.postValue(View.GONE)
                    _setError.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    Log.i("changePassword", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }
}