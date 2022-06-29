package com.rino.self_services.ui.myProfile

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.complaints.ComplaintResponse
import com.rino.self_services.model.pojo.complaints.CreateComplaintRequest
import com.rino.self_services.model.pojo.profile.ProfileResponse
import com.rino.self_services.model.reposatory.UserRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
@HiltViewModel
class MyProfileViewModel  @Inject constructor(private val modelRepository: UserRepo) : ViewModel() {
    private  var _profileData = MutableLiveData<ProfileResponse>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val profileData: LiveData<ProfileResponse>
        get() = _profileData

    fun getProfileData(){
        Log.d("getProfileData","call")
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.getProfileData()) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    result.data.let {
                        Log.d("atchments","done")
                        if (it != null) {
                            _profileData.postValue(it)
                        }
                    }
                    Log.i("see All network:", "done")
                }
                is Result.Error -> {
                    Log.e(" error:", "${result.exception.message}")
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