package com.rino.self_services.ui.paymrntArchive

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.pojo.amountChangelog.AmountChangelogResponse
import com.rino.self_services.model.reposatory.PaymentRepo
import com.rino.self_services.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentArchiveViewModel @Inject constructor(private val modelRepository: PaymentRepo) : ViewModel(){
    private var _amountChangelogResponse = MutableLiveData<AmountChangelogResponse>()
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError
    val amountChangelogResponse: LiveData<AmountChangelogResponse>
        get() = _amountChangelogResponse


    fun getAmountChangelog(id:Int){
        Log.d("getAmountChangelog","call")
        _loading.postValue(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = modelRepository.getAmountChangelog(id)) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    result.data.let {
                        Log.d("getAmountChangelog","done")
                        if (it != null) {
                            _amountChangelogResponse.postValue(it)
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