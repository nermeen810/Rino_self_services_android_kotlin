package com.rino.self_services.ui.seeAllHr


import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.HRSeeAllData
import com.rino.self_services.model.pojo.HRSeeAllItem

import com.rino.self_services.model.reposatory.HrClearanceRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class SeeAllHRClearanceViewModel@Inject constructor(private  val repo: HrClearanceRepo): ViewModel() {
    private  var _seeAllData = MutableLiveData<HRSeeAllData>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    var pageNumber:Int = 1

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val seeAllPaymentProcessData: LiveData<HRSeeAllData>
        get() = _seeAllData
    var arrayList = ArrayList<HRSeeAllItem>()
    fun getData(hrClearanceRequest: HRClearanceRequest){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getHRClearanceAllRecords(HRClearanceRequest(hrClearanceRequest.from,hrClearanceRequest.to,hrClearanceRequest.meOrOthers,pageNumber)) ) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data?.let {
                            arrayList.addAll(it.date)
                            _seeAllData.postValue(it)
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

}
data class HRClearanceRequest(var from:String,var to:String,var meOrOthers:String,var pageNumber:Int)