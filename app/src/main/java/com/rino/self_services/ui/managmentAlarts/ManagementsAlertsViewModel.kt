package com.rino.self_services.ui.managmentAlarts

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsResponse
import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsSearchResponse
import com.rino.self_services.model.pojo.notifications.NotificationCountResponse
import com.rino.self_services.model.reposatory.ManagementsAlertsRepo
import com.rino.self_services.model.reposatory.NotificationRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagementsAlertsViewModel @Inject constructor(private val managementsAlertsRepo: ManagementsAlertsRepo,   private val notificationRepo: NotificationRepo) :
    ViewModel() {
    private var _setError = MutableLiveData<String>()
    private var _noData = MutableLiveData<Boolean>()
    private var _loading = MutableLiveData<Int>()
    private var _getPaymentData = MutableLiveData<ManagementAlertsResponse?>()
    private var _getNotificationCount = MutableLiveData<NotificationCountResponse?>()
    private var _getSearchHistoryData = MutableLiveData<ManagementAlertsSearchResponse?>()
//    private var _navToSeeAll: MutableLiveData<NavSeeAll> = MutableLiveData()
//    private var _navToTaskDetails: MutableLiveData<HRClearanceDetailsRequest> = MutableLiveData()

    companion object {

        var periodTimeList_en =
            arrayListOf(
                "twoyearsago",
                "lastyear",
                "year",
                "lastmonth",
                "month",
                "lastweek",
                "week",
                "all"
            )
        var lastSelectedPos = periodTimeList_en.size - 1
    }

//    val navToTaskDetails: LiveData<HRClearanceDetailsRequest>
//        get() = _navToTaskDetails
//
    val getSearchHistoryData: LiveData<ManagementAlertsSearchResponse?>
        get() = _getSearchHistoryData
//
//    val navToSeeAll: LiveData<NavSeeAll>
//        get() = _navToSeeAll

    val loading: LiveData<Int>
        get() = _loading

    val setError: LiveData<String>
        get() = _setError

    val noData: LiveData<Boolean>
        get() = _noData

    val getPaymentData: LiveData<ManagementAlertsResponse?>
        get() = _getPaymentData

    val getNotificationCount: LiveData<NotificationCountResponse?>
        get() = _getNotificationCount
//
//    fun navToSeeAll(navSeeAll: NavSeeAll) {
//        _navToSeeAll.value = navSeeAll
//    }
//
//    fun navToServiceDetails(item: HRClearanceDetailsRequest) {
//        _navToTaskDetails.value = item
//    }

    fun getManagementAlertsData() {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = managementsAlertsRepo.getManagementAlertsList(periodTimeList_en[lastSelectedPos])) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    Log.i("getManagementAlertsList:", "${result.data}")
                    if (result.data?.data?.size == 0) {
                        _noData.postValue(true)
                    } else {
                        _getPaymentData.postValue(result.data)
                    }

                }
                is Result.Error -> {
                    Log.e("getManagementAlertsList:", "${result.exception.message}")
                    _setError.postValue(result.exception.message)
                    _loading.postValue(View.GONE)

                }
                is Result.Loading -> {
                    Log.i("getManagementAlertsList", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }

    fun searchMARequest(searchTxt: String) {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = managementsAlertsRepo.searchMARequest(searchTxt)) {
                is Result.Success -> {
                    // _loading.postValue(View.GONE)
                    Log.i("searchMARequest:", "${result.data}")
                    _loading.postValue(View.GONE)

                    if (result.data?.data?.size == 0) {
                        _noData.postValue(true)
                    } else {
                        _getSearchHistoryData.postValue(result.data)
                    }
                }
                is Result.Error -> {
                    Log.e("searchMARequest:", "${result.exception.message}")
                    _noData.postValue(true)
                    _loading.postValue(View.GONE)

                }
                is Result.Loading -> {
                    Log.i("searchMARequest", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }

    }

    fun getNotificationCount() {
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


    fun viewLoading(loading: Int) {
        _loading.value = loading
    }




}