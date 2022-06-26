package com.rino.self_services.ui.viewComplints

import androidx.lifecycle.ViewModel
import com.rino.self_services.model.reposatory.ComplaintsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewComplaintsViewModel  @Inject constructor(private val complaintsRepo: ComplaintsRepo) : ViewModel() {
}