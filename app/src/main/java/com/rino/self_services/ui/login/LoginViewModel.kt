package com.rino.self_services.ui.login

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.rino.self_services.model.dataSource.localDataSource.MySharedPreference
import com.rino.self_services.model.dataSource.localDataSource.Preference
import com.rino.self_services.model.dataSource.localDataSource.PreferenceDataSource
import com.rino.self_services.model.pojo.login.ChangePasswordRequest
import com.rino.self_services.model.pojo.login.LoginRequest
import com.rino.self_services.model.pojo.notifications.DeviceTokenRequest
import com.rino.self_services.model.reposatory.UserRepo
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(private val modelRepository: UserRepo, private val context: Application) :
    ViewModel() {
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    )

    private val sharedPreference: Preference = PreferenceDataSource(preference)

    private var _isLogin = MutableLiveData<Boolean>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    private var _navigationToHome = MutableLiveData<Boolean>()
    private var _setDeviceTokenResponse = MutableLiveData<Boolean>()
  //  private var token = ""
  //  private var deviceId = ""


    val isLogin: LiveData<Boolean>
        get() = _isLogin

    val setDeviceTokenResponse: LiveData<Boolean>
        get() = _setDeviceTokenResponse

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

    fun setDeviceToken() {

        getFirebaseToken()
//            viewModelScope.launch(Dispatchers.IO) {
//                    getFirebaseToken()
//                    Log.e(" token:", "${token}, id: ${deviceId}")
//
//                when (val result = modelRepository.setDeviceToken(DeviceTokenRequest(token,deviceId))) {
//
//                            is Result.Success -> {
//                 //       _loading.postValue(View.GONE)
//                                Log.d("setDeviceToken", "call")
//
//                                result.data.let {
//                            _setDeviceTokenResponse.postValue(true)
//                        }
//                    }
//                    is Result.Error -> {
//                        Log.e(" setDeviceTokenError:", "${result.exception.message}")
//                    //    _loading.postValue(View.GONE)
//                        _setError.postValue(result.exception.message)
//                    }
//                    is Result.Loading -> {
//                        Log.i("setDeviceToken", "Loading")
//                //        _loading.postValue(View.VISIBLE)
//                    }
//                }
//
//        }
    }

    private fun getDeviceID():String {
       val deviceId =Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        Log.w("FirebaseDeviceId",deviceId )
       return deviceId
    }

   private  fun getFirebaseToken() {


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FirebaseToken", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            else {
                val token = task.result ?: ""
                Log.d("FirebaseToken", token)
                viewModelScope.launch(Dispatchers.IO) {
                    //               Log.e(" token:", "${token}, id: ${deviceId}")

                    when (val result =
                        modelRepository.setDeviceToken(DeviceTokenRequest(token, getDeviceID()))) {

                        is Result.Success -> {
                            //       _loading.postValue(View.GONE)
                            Log.d("setDeviceToken", "call")

                            result.data.let {
                                _setDeviceTokenResponse.postValue(true)
                            }
                        }
                        is Result.Error -> {
                            Log.e(" setDeviceTokenError:", "${result.exception.message}")
                            //    _loading.postValue(View.GONE)
                            _setError.postValue(result.exception.message)
                        }
                        is Result.Loading -> {
                            Log.i("setDeviceToken", "Loading")
                            //        _loading.postValue(View.VISIBLE)
                        }
                    }
                }
            }
            // Get new FCM registration token

       //     Toast.makeText(context, "token: ${token.toString()}", Toast.LENGTH_SHORT).show()
        })

    }
}