package com.rino.self_services.ui.hrClearanceHome

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.hrClearance.HrClearanceResponse
import com.rino.self_services.model.pojo.payment.SearchResponse
import com.rino.self_services.model.reposatory.HrClearanceRepo
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HrClearanceViewModel  @Inject constructor(private val modelRepository: HrClearanceRepo, private val context: Application) : ViewModel() {
    private var _setError = MutableLiveData<String>()
    private var _noData = MutableLiveData<Boolean>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    private var _getPaymentData = MutableLiveData<HrClearanceResponse?>()
    private var _getSearchHistoryData = MutableLiveData<SearchResponse?>()
    private var _navToSeeAll: MutableLiveData<NavSeeAll> = MutableLiveData()
    //       private var _navToTaskDetails: MutableLiveData<ServiceData> = MutableLiveData()


    companion object {
        var me_or_others = "me"
        var periodTimeList_en =
            arrayListOf("twoyearsago","lastyear","year","lastmonth","month","lastweek","week","all")
        var lastSelectedPos = periodTimeList_en.size-1
    }
//        val navToTaskDetails: LiveData<ServiceData>
//        get() = _navToTaskDetails

    val getSearchHistoryData: LiveData<SearchResponse?>
        get() = _getSearchHistoryData

    val navToSeeAll: LiveData<NavSeeAll>
        get() = _navToSeeAll

    val loading: LiveData<Int>
        get() = _loading

    val setError: LiveData<String>
        get() = _setError

    val noData: LiveData<Boolean>
        get() = _noData

    val getPaymentData: LiveData<HrClearanceResponse?>
        get() = _getPaymentData

    fun navToSeeAll(navSeeAll: NavSeeAll)
    {
        _navToSeeAll.value = navSeeAll
    }

//        fun navToServiceDetails(item: ServiceData) {
//            _navToTaskDetails.value = item
//        }

    fun getPaymentData() {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("lastSelectedPosHr:",  periodTimeList_en[lastSelectedPos])
            when (val result = modelRepository.getHrClearanceHomeList(me_or_others,periodTimeList_en[lastSelectedPos])) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    Log.i("getPaymentData:", "${result.data}")
                    if(result.data?.data?.size==0)
                    {
                        _noData.postValue(true)
                    }
                    else {
                        _getPaymentData.postValue(result.data)
                    }

                }
                is Result.Error -> {
                    Log.e("getPaymentData:", "${result.exception.message}")
                    _setError.postValue(result.exception.message)
                    _loading.postValue(View.GONE)

                }
                is Result.Loading -> {
                    Log.i("getPaymentData", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }

//    fun searchHistoryDataByService(searchTxt:String) {
//        _loading.postValue(View.VISIBLE)
//        viewModelScope.launch(Dispatchers.IO) {
//            when (val result = modelRepository.searchRequest(searchTxt)) {
//                is Result.Success -> {
//                    // _loading.postValue(View.GONE)
//                    Log.i("searchHistoryDataByService:", "${result.data}")
//                    _getSearchHistoryData.postValue(result.data)
//                    _loading.postValue(View.GONE)
//
//                }
//                is Result.Error -> {
//                    Log.e("searchHistoryDataByService:", "${result.exception.message}")
//                    _noData.postValue(true)
//                    _loading.postValue(View.GONE)
//
//                }
//                is Result.Loading -> {
//                    Log.i("searchHistoryDataByService", "Loading")
//                    _loading.postValue(View.VISIBLE)
//                }
//            }
//        }
//
//    }

    fun viewLoading(loading:Int){
        _loading.value = loading
    }


}