package com.rino.self_services.ui.seeAllPayment

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
import com.rino.self_services.model.pojo.Item
import com.rino.self_services.model.pojo.SeeAllRequest
import com.rino.self_services.model.pojo.SeeAllPaymentProcessResponse
import com.rino.self_services.model.reposatory.PaymentRepo
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SeeAllPaymentProcessViewModel @Inject constructor(private  val repo: PaymentRepo, private val context: Application):ViewModel() {
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE))

    private val sharedPreference: Preference = PreferenceDataSource(preference)

    private  var _seeAllData = MutableLiveData<SeeAllPaymentProcessResponse>()

    var seeAllarray = ArrayList<Item?>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>()
     var pageNumber:Long = 1
    var totalPages = 1
    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val seeAllPaymentProcessData: LiveData<SeeAllPaymentProcessResponse>
        get() = _seeAllData
    fun getData(seeAllRequest:SeeAllRequest){

        var mySeeAllRequest = SeeAllRequest("Bearer "+sharedPreference.getToken(),seeAllRequest.currentFutuer,seeAllRequest.me,seeAllRequest.from,seeAllRequest.to,pageNumber)
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = mySeeAllRequest?.let { repo.getAllRecords(it) }) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data?.let {
                            if (seeAllarray.isEmpty()){
                                calculatePagesNumber(it.totalItems)
                            }
                            _seeAllData.postValue(it)
                            seeAllarray.addAll(it.data)
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
    fun calculatePagesNumber(total:Int){
        val temp:Float = total.toFloat() / 20
        val reminder = (temp - temp.toInt())
                val wholeNumber = temp.toInt()
                if(wholeNumber < 1 && wholeNumber > 0){

                    totalPages = 1
                    Log.d("totalPages",totalPages.toString())
                }
                else if (reminder > 0 ){

                    totalPages = (total.toDouble() /20.0).toInt()+1
                    Log.d("totalPages",totalPages.toString())
                }else{
                    Log.d("totalPages",totalPages.toString())
                    totalPages = (total.toDouble() /20.0).toInt()
                }

    }
}