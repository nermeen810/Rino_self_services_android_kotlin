package com.rino.self_services.ui.complaints

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.complaints.ComplaintResponse
import com.rino.self_services.model.pojo.complaints.CreateComplaintRequest
import com.rino.self_services.model.reposatory.ComplaintsRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject
@HiltViewModel
class ComplaintsViewModel @Inject constructor( private val complaintsRepo: ComplaintsRepo) : ViewModel() {
    private  var _createComplaintResponse = MutableLiveData<ComplaintResponse>()
    private  var _departmentsList = MutableLiveData<ArrayList<String>>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    private var _attachmentsDeleteItem = MutableLiveData<File>()
    private var _navToPdfPreview = MutableLiveData<File>()

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val createComplaintResponse: LiveData<ComplaintResponse>
        get() = _createComplaintResponse
    val departmentsList: LiveData<ArrayList<String>>
        get() = _departmentsList
    val attachmentsDeleteItem: LiveData<File>
        get() = _attachmentsDeleteItem
    val navToPdfPreview: LiveData<File>
        get() = _navToPdfPreview


    fun setAttachmentsDeleteItem(attachmentItem: File) {
        _attachmentsDeleteItem.value = attachmentItem
    }
    fun setNavToPdfPreview(attachmentItem: File) {
        _navToPdfPreview.value = attachmentItem
    }
    fun getDepartment() {
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = complaintsRepo.getDepartmentList()) {
                is Result.Success -> {
                    result.data.let {
                        _departmentsList.postValue(it)
                        _loading.postValue(View.GONE)
                        Log.i("getDepartment:", "${result.data}")
                    }

                }
                is Result.Error -> {
                    Log.e("getDepartment:", "${result.exception.message}")
                    _loading.postValue(View.GONE)
                    _setError.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    Log.i("getDepartment", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }

    }

    fun createComplaint(department:String,officer:String, body:String, parts:List<MultipartBody.Part>?){
        Log.d("createComplaintCall","call")
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = complaintsRepo.createComplaint(CreateComplaintRequest(department,officer,body,parts))) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                        result.data.let {
                            Log.d("atchments","done")
                            if (it != null) {
                               _createComplaintResponse.postValue(it)
                            }
                        }
                        Log.i("see All network:", "done")
                    }
                is Result.Error -> {
                    Log.e(" error:", "${result.exception.message}")
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