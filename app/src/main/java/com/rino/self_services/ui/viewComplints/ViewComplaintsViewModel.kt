package com.rino.self_services.ui.viewComplints

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.model.pojo.complaints.Attchements
import com.rino.self_services.model.pojo.complaints.ComplaintItemResponse
import com.rino.self_services.model.reposatory.ComplaintsRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewComplaintsViewModel  @Inject constructor(private val complaintsRepo: ComplaintsRepo) : ViewModel() {
    private var _complaintResponse = MutableLiveData<ArrayList<ComplaintItemResponse>>()
    private var _navToViewAttachments = MutableLiveData<ArrayList<Attchements>>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val complaintResponse: LiveData<ArrayList<ComplaintItemResponse>>
        get() = _complaintResponse
    val navToViewAttachments: LiveData<ArrayList<Attchements>>
        get() = _navToViewAttachments

    fun navToAttachments(attachments: ArrayList<Attchements>)
    {
        _navToViewAttachments.value = attachments
    }

    fun getComplaintsList(){
        Log.d("getComplaintsList","call")
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = complaintsRepo.getComplaintsList()) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    result.data.let {
                        Log.d("atchments","done")
                        if (it != null) {
                            _complaintResponse.postValue(it)
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