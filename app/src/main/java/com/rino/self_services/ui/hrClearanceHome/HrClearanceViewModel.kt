package com.rino.self_services.ui.hrClearanceHome

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.model.pojo.hrClearance.HrClearanceResponse
import com.rino.self_services.model.pojo.hrClearance.SearchHrResponse
import com.rino.self_services.model.pojo.notifications.NotificationCountResponse
import com.rino.self_services.model.pojo.payment.SearchResponse
import com.rino.self_services.model.reposatory.HrClearanceRepo
import com.rino.self_services.model.reposatory.NotificationRepo
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HrClearanceViewModel  @Inject constructor(private val modelRepository: HrClearanceRepo, private  val notificationRepo: NotificationRepo) : ViewModel() {
    private var _setError = MutableLiveData<String>()
    private var _noData = MutableLiveData<Boolean>()
    private var _loading = MutableLiveData<Int>()
    private var _getPaymentData = MutableLiveData<HrClearanceResponse?>()
    private var _getNotificationCount = MutableLiveData<NotificationCountResponse?>()
    private var _getSearchHistoryData = MutableLiveData<SearchHrResponse?>()
    private var _navToSeeAll: MutableLiveData<NavSeeAll> = MutableLiveData()
    private var _navToTaskDetails: MutableLiveData<HRClearanceDetailsRequest> = MutableLiveData()
    var me_or_others = "me"

    companion object {

        var periodTimeList_en =
            arrayListOf("twoyearsago","lastyear","year","lastmonth","month","lastweek","week","all")
        var lastSelectedPos = periodTimeList_en.size-1
    }
        val navToTaskDetails: LiveData<HRClearanceDetailsRequest>
        get() = _navToTaskDetails

    val getSearchHistoryData: LiveData<SearchHrResponse?>
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

    val getNotificationCount: LiveData<NotificationCountResponse?>
        get() = _getNotificationCount

    fun navToSeeAll(navSeeAll: NavSeeAll)
    {
        _navToSeeAll.value = navSeeAll
    }

        fun navToServiceDetails(item: HRClearanceDetailsRequest) {
            _navToTaskDetails.value = item
        }

    fun getHrData(me_or_other :String) {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("me_or_others:",me_or_others)
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
    fun searchHrRequest(searchTxt:String) {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.searchRequest(searchTxt)) {
                is Result.Success -> {
                    // _loading.postValue(View.GONE)
                    Log.i("searchHistoryDataByService:", "${result.data}")
                    _loading.postValue(View.GONE)

                    if(result.data?.data?.size==0)
                    {
                        _noData.postValue(true)
                    }
                    else {
                        _getSearchHistoryData.postValue(result.data)
                    }
                }
                is Result.Error -> {
                    Log.e("searchHistoryDataByService:", "${result.exception.message}")
                    _noData.postValue(true)
                    _loading.postValue(View.GONE)

                }
                is Result.Loading -> {
                    Log.i("searchHistoryDataByService", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }

    }

    fun getNotificationCount(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = notificationRepo.getNotificationCount()) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    Log.i("getNotificationCount:", "${result.data}")
                    _getNotificationCount.postValue(result.data)
                }

                is Result.Error -> {
                    Log.e("getNotificationCount:", "${result.exception.message}")

                }
                is Result.Loading -> {
                    Log.i("getNotificationCount", "Loading")
                }
            }
        }
    }


    fun viewLoading(loading:Int){
        _loading.value = loading
    }


}