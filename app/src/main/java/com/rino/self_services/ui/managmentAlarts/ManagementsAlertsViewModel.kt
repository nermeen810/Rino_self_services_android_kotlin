package com.rino.self_services.ui.managmentAlarts

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rino.self_services.model.reposatory.ManagementsAlertsRepo
import javax.inject.Inject

class ManagementsAlertsViewModel @Inject constructor(private val managementsAlertsRepo: ManagementsAlertsRepo) : ViewModel() {
    companion object {

        var periodTimeList_en =
            arrayListOf("twoyearsago","lastyear","year","lastmonth","month","lastweek","week","all")
        var lastSelectedPos = periodTimeList_en.size-1
    }
    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)

    val loading: LiveData<Int>
        get() = _loading
    val setError: LiveData<String>
        get() = _setError



}