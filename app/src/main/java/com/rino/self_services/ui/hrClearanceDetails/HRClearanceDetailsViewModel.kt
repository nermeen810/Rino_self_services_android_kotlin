package com.rino.self_services.ui.hrClearanceDetails

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.HRClearanceDetails
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
//import com.rino.self_services.model.reposatory.HRClearanceRepo
import com.rino.self_services.model.reposatory.HrClearanceRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HRClearanceDetailsViewModel@Inject constructor(private  val repo: HrClearanceRepo):ViewModel(){
    private  var _detailsData = MutableLiveData<HRClearanceDetails>()
    private  var _action = MutableLiveData<String>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    val loading: LiveData<Int>
        get() = _loading
    val action: LiveData<String>
        get() = _action
    val setError: LiveData<String>
        get() = _setError
    val detailsData: LiveData<HRClearanceDetails>
        get() = _detailsData

    fun getData(hrClearanceDetailsRequest: HRClearanceDetailsRequest){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getHRDetails(hrClearanceDetailsRequest)) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data.let {
                            _detailsData.postValue(it)
                        }
                        Log.i("see All network:", "done")
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
    fun actionApproveOrDeny(entity :Int?,
                id :Int?, action :String){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.postActionApproveOrDeny(entity,id,action)) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data.let {
                            if(action=="approve") {
                                _action.postValue("approve")
                            }
                            else if(action=="deny"){
                                _action.postValue("deny")
                            }
                        }
                        Log.i("see All network:", "done")
                    }
                }
                is Result.Error -> {
                    Log.e("actionApproveOrDeny:", "${result.exception.message}")
                    _loading.postValue(View.GONE)
                    _setError.postValue(result.exception.message)


                }
                is Result.Loading -> {
                    Log.i("actionApproveOrDeny", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }
}