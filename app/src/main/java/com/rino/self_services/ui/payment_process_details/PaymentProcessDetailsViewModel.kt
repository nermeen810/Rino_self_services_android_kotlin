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
import com.rino.self_services.model.pojo.ActionResponse
import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.model.pojo.CreateAttachmentRequest
import com.rino.self_services.model.pojo.PaymentProcessDetails
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

    val action: LiveData<ActionResponse>
        get() = _actionData
    var _actionData = MutableLiveData<ActionResponse>()
    var attachments = ArrayList<Attachment>()
    fun getData(id:Int){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getPaymentDetails("Bearer "+sharedPreference.getToken(),id) ) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data?.let {
                            _detailsData.postValue(it)
                            attachments.clear()
                            it.data?.attachments?.let { it1 -> attachments.addAll(it1) }
                        }
                    }
                }
                is Result.Error -> {
                    Log.i("error", "error")
                    _loading.postValue(View.GONE)
                    _setError.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    Log.i("details", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }
    fun createAttachment(part: ArrayList<MultipartBody.Part>?, id:Int){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.createAttachment(CreateAttachmentRequest(id,0,part))) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data.let {
//                            it?.data?.let { it1 -> Log.d("newAyman", it1.date) }
//                            _detailsData.postValue(it)
                            if (it != null) {
                                attachments.addAll(it)
                            }
                            _setToTrue.postValue(true)
//                            _detailsData.postValue(it)
                        }
                    }
                }
                is Result.Error -> {
                    _loading.postValue(View.GONE)
                    _setToTrue.postValue(false)
//                    _setError.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }
    fun createAction(id:Int,action:String){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.paymentAction(id,action)) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data.let {
                            _actionData.postValue(it)
                        }
                    }
                }
                is Result.Error -> {
                    _loading.postValue(View.GONE)
//                    _setToTrue.postValue(false)
                    _setError.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }
}