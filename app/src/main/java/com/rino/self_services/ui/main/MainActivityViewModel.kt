package com.rino.self_services.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rino.self_services.model.reposatory.NotificationRepo
import com.rino.self_services.model.reposatory.PaymentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel
@Inject constructor(private val modelRepository: PaymentRepo,private val notificationRepo: NotificationRepo) : ViewModel() {

    fun isLogin():Boolean{
        return  modelRepository.isLogin()
    }
    fun markNotificationAsRead(notificationID:Int){
        viewModelScope.launch(Dispatchers.IO) {
             when (val result =  notificationRepo.setNotificationAsRead(notificationID)) {
             }
            }
        }
    }
