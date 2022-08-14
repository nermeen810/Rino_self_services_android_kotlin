package com.rino.self_services.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.login.PermissionResponse
import com.rino.self_services.model.reposatory.ComplaintsRepo
import com.rino.self_services.model.reposatory.UserRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val modelRepository: ComplaintsRepo):ViewModel(){
    private var _getPermission = MutableLiveData<PermissionResponse?>()
    private var _setError = MutableLiveData<String>( )

    val setError: LiveData<String>
        get() = _setError

    val getPermission: LiveData<PermissionResponse?>
        get()  =_getPermission

    fun getPermissions() {

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.getPermission()) {
                is Result.Success -> {
                    Log.i("getPermission:", "${result.data}")
                    _getPermission.postValue(result.data)
                }
                is Result.Error -> {
                    Log.e("getPermission:", "${result.exception.message}")
                    _setError.postValue(result.exception.message)

                }
                is Result.Loading -> {
                    Log.i("getPermission", "Loading")
                }
            }
        }
    }
}