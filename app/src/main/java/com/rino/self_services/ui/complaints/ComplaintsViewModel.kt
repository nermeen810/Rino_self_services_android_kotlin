package com.rino.self_services.ui.complaints

import androidx.lifecycle.ViewModel
import com.rino.self_services.model.reposatory.ComplaintsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ComplaintsViewModel @Inject constructor( private val complaintsRepo: ComplaintsRepo) : ViewModel() {

}