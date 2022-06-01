package com.rino.self_services.ui.login

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.pojo.LoginRequest
import com.rino.self_services.model.reposatory.UserRepo
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
 @Inject constructor(private val modelRepository: UserRepo, private val context: Application) : ViewModel() {
 private val preference = MySharedPreference(
   context.getSharedPreferences(
    PREF_FILE_NAME,
    Context.MODE_PRIVATE))

 private val sharedPreference: Preference = PreferenceDataSource(preference)

 private  val _isLogin = MutableLiveData<Boolean> ()
 private var _setError = MutableLiveData<String>()
 private var _loading = MutableLiveData<Int>(View.GONE)
 private  val _navigationToHome: MutableLiveData<Boolean> = MutableLiveData()

 val isLogin: LiveData<Boolean>
  get() = _isLogin

 val navigationToHome: LiveData<Boolean>
  get() = _navigationToHome

 val loading: LiveData<Int>
  get() = _loading

 val setError: LiveData<String>
  get() = _setError


 fun login(loginRequest: LoginRequest?) {
  _loading.postValue(View.VISIBLE)
  viewModelScope.launch(Dispatchers.IO) {
   when (val result = loginRequest?.let { modelRepository.login(it) }) {
    is Result.Success -> {
     _loading.postValue(View.GONE)
      withContext(Dispatchers.Main) {
       _isLogin.postValue(true)
       sharedPreference.setLogin(true)
       result.data?.token?.let { sharedPreference.setToken(it) }
       result.data?.refreshToken?.let { sharedPreference.setRefreshToken(it) }
       Log.i("login:", "valid email")
      }
    }
    is Result.Error -> {
     Log.e("login:", "${result.exception.message}")
     _loading.postValue(View.GONE)
     _setError.postValue(result.exception.message)


    }
    is Result.Loading -> {
     Log.i("login", "Loading")
     _loading.postValue(View.VISIBLE)
    }
   }
  }

 }
}