package com.rino.self_services.ui.myProfile

import android.app.Application
import androidx.lifecycle.ViewModel
import com.rino.self_services.model.reposatory.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MyProfileViewModel  @Inject constructor(private val modelRepository: UserRepo) : ViewModel() {

}