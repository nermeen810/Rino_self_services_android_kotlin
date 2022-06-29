package com.rino.self_services.ui.hrClearanceDetails


import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.*
//import com.rino.self_services.model.reposatory.CreateAttachmentRequest
import com.rino.self_services.model.reposatory.HrClearanceRepo
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class HRClearanceDetailsViewModel@Inject constructor(private  val repo: HrClearanceRepo):ViewModel(){
    private  var _detailsData = MutableLiveData<HRClearanceDetails>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>()
    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val detailsData: LiveData<HRClearanceDetails>
        get() = _detailsData
    val action: LiveData<ActionResponse>
        get() = _actionData
    var _actionData = MutableLiveData<ActionResponse>()
    var attachments = ArrayList<Attachment>()
    private var _isTrue = MutableLiveData<Boolean>()
    val isTrue: LiveData<Boolean>
        get() = _isTrue
    fun getData(hrClearanceDetailsRequest: HRClearanceDetailsRequest){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.getHRDetails(hrClearanceDetailsRequest)) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data?.let {
                            attachments.clear()
                            _detailsData.postValue(it)
                            attachments.addAll(it.data.attachment)
                        }
//                        Log.i("see All network:", "done")
                    }
                }
                is Result.Error -> {
//                    Log.e("login:", "${result.exception.message}")
                    _loading.postValue(View.GONE)
                    _setError.postValue(result.exception.message)


                }
                is Result.Loading -> {
//                    Log.i("login", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }
    }
fun createAttachment(part:List<MultipartBody.Part>?, id:Int, entity:Int){
        Log.d("startcall","call")
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.createAttachment(CreateAttachmentRequest(id,entity,part))) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data.let {
                            Log.d("atchments","done")
                            if (it != null) {
                                attachments.addAll(it)
                                _isTrue.postValue(true)
                            }
//                           _detailsData.postValue(it)
                        }
                        Log.i("see All network:", "done")
                    }
                }
                is Result.Error -> {
                    Log.e("ayman error:", "${result.exception.message}")
                    _isTrue.postValue(false)
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
    fun createAction(id:Int,action:String,entity: Int){
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repo.clearanceAction(id,action,entity)) {
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