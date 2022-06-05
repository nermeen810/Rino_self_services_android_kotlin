package com.rino.self_services.ui.forgetPassword

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.forgetPassword.RequestOTP
import com.rino.self_services.model.pojo.forgetPassword.ResetPasswordRequest
import com.rino.self_services.model.pojo.forgetPassword.ResponseOTP
import com.rino.self_services.model.reposatory.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.rino.self_services.utils.Result
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel
@Inject constructor (private val modelRepository: UserRepo, private val context: Application) : ViewModel() {


    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    private val _getOTP = MutableLiveData<ResponseOTP?>()
    private val _resetPass = MutableLiveData<ResponseOTP?>()

    val resetPass: LiveData<ResponseOTP?>
        get() = _resetPass

    val getOTP: LiveData<ResponseOTP?>
        get() = _getOTP

    val loading: LiveData<Int>
        get() = _loading

    val setError: LiveData<String>
        get() = _setError

    fun requestOTP(email: String) {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.requestOTP(RequestOTP(email))) {
                is Result.Success -> {
                    _getOTP.postValue(result.data)
                    _loading.postValue(View.GONE)
                    Log.i("requestOTP:", "${result.data}")
                }
                is Result.Error -> {
                    Log.e("requestOTP:", "${result.exception.message}")
                    _loading.postValue(View.GONE)
                    _setError.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    Log.i("requestOTP", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }

    }

    fun resetPass(email: String, otp: String, newPass: String) {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result =
                modelRepository.resetPassword(ResetPasswordRequest(email, otp, newPass))) {
                is Result.Success -> {
                    _resetPass.postValue(result.data)
                    _loading.postValue(View.GONE)
                    Log.i("resetPass:", "${result.data}")
                }
                is Result.Error -> {
                    Log.e("resetPass:", "${result.exception.message}")
                    _loading.postValue(View.GONE)
                    _setError.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    Log.i("resetPass", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }

    }

}