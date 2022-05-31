package com.rino.self_services.ui.seeAllPayment

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.SeeAllRequest
import com.rino.self_services.model.pojo.SeeAllPaymentProcessResponse
import com.rino.self_services.model.reposatory.PaymentRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SeeAllPaymentProcessViewModel @Inject constructor(private  val repo: PaymentRepo):ViewModel() {
    private  var _seeAllData = MutableLiveData<SeeAllPaymentProcessResponse>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val seeAllPaymentProcessData: LiveData<SeeAllPaymentProcessResponse>
        get() = _seeAllData
val token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjI2Nzc5NTM5MjY1MDZDNjhGMTkyMzIyMkExMUYwNDA4RjYxQTU5MTYiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJKbmVWT1NaUWJHanhraklpb1I4RUNQWWFXUlkifQ.eyJuYmYiOjE2NTQwMTMzNDUsImV4cCI6MTY1NDAxNjk0NSwiaXNzIjoiaHR0cHM6Ly9yaW5vLWFwcC1zdGFnaW5nLmF6dXJld2Vic2l0ZXMubmV0IiwiYXVkIjpbInJoaW5vLmFwaSIsIlJoaW5vLklkZW50aXR5QVBJIl0sImNsaWVudF9pZCI6IlJoaW5vLkFuZHJvaWQiLCJzdWIiOiI4Y2NkNDFmZC00MzRhLTRhZDQtYTEwYy0zY2Q3ZTA1ZDA0ZDYiLCJhdXRoX3RpbWUiOjE2NTQwMTMzNDUsImlkcCI6ImlkZW50aXR5IiwianRpIjoiQ0FDMkVBNzJFRkE2OEMxMDY0MzVGQTExRjk2RTAxMTEiLCJpYXQiOjE2NTQwMTMzNDUsInNjb3BlIjpbIm9wZW5pZCIsInByb2ZpbGUiLCJyaGluby5hcGkiLCJSaGluby5JZGVudGl0eUFQSSIsIm9mZmxpbmVfYWNjZXNzIl0sImFtciI6WyJpZGVudGl0eSJdfQ.pOH-qz_UvCngHyoHu2rh3TbROrZvhqYpHz02WT26e2Br5cON9hIHHxa7uqA5J7sQ-bSU6qMJThvNTzImZHjBkvKZ5U6-iejsOM7gfXnQcCpSEFyYj7qGtcRnzDThwlNvR2SSaBdl6PPRrlrT_RYb6p0KCYqsmCTDrtPChwu_ttyku5BgBCgGsOI0vqnIWPazDbLFjIiFHP4tiivmM-kfFLj0WNsWJrNkHrqcWwSMyRnM2Y21s7G07WXCtpu4pCUrQgzcN-gIVFdSr7IgPZRmtS7KyrhD2tUgXrudVlanB0-LRkZDzy0eM9apEJWQy7gIR1nBbi2g9ubs6n-P_iLkjg"
    fun getData(){


        var mySeeAllRequest = SeeAllRequest(token,"requests","me","2020-01-01","2020-12-30",1)
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = mySeeAllRequest?.let { repo.getAllRecords(it) }) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    withContext(Dispatchers.Main) {
                        result.data?.let {
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