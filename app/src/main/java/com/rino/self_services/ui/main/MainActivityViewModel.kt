package com.rino.self_services.ui.main

import androidx.lifecycle.ViewModel
import com.rino.self_services.model.reposatory.PaymentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel
@Inject constructor(private val modelRepository: PaymentRepo) : ViewModel() {

    fun isLogin():Boolean{
        return  modelRepository.isLogin()
    }
}