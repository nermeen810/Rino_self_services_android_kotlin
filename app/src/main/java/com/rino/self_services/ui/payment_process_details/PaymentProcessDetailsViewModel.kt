package com.rino.self_services.ui.payment_process_details

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
import com.rino.self_services.model.pojo.PaymentProcessDetails
import com.rino.self_services.model.reposatory.CreateAttachmentForPaymentRequest
import com.rino.self_services.model.reposatory.CreateAttachmentRequest
import com.rino.self_services.model.reposatory.PaymentRepo
import com.rino.self_services.utils.PREF_FILE_NAME
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject
@HiltViewModel
class PaymentProcessDetailsViewModel@Inject constructor(private  val repo: PaymentRepo, private val context: Application):
    ViewModel() {
    private val preference = MySharedPreference(
        context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE))

    private val sharedPreference: Preference = PreferenceDataSource(preference)

    private  var _detailsData = MutableLiveData<PaymentProcessDetails>()

    private var _setError = MutableLiveData<String>()
    private var _setToTrue = MutableLiveData<Boolean>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    val setToTrue: LiveData<Boolean>
    get() = _setToTrue
    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val detailsData: LiveData<PaymentProcessDetails>
        get() = _detailsData
    fun getData(id:Int){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getPaymentDetails("Bearer "+sharedPreference.getToken(),id) ) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data?.let {
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
    fun createAttachment(part: MultipartBody.Part?, id:Int,action:String){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.createAttachment(CreateAttachmentForPaymentRequest(action,id,part))) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data.let {
//                            it?.data?.let { it1 -> Log.d("newAyman", it1.date) }
                            _detailsData.postValue(it)
                            _setToTrue.postValue(true)

                            Log.d("atchments","done")
//                            _detailsData.postValue(it)
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