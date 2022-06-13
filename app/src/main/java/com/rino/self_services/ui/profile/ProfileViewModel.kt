package com.rino.self_services.ui.profile


import androidx.lifecycle.ViewModel
import com.rino.self_services.model.reposatory.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel  @Inject constructor(private val modelRepository: UserRepo) : ViewModel() {

    fun logout() {
        modelRepository.logout()
    }
}