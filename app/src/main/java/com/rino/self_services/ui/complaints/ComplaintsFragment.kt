package com.rino.self_services.ui.complaints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentComplaintsBinding
import com.rino.self_services.databinding.FragmentProfileBinding
import com.rino.self_services.ui.profile.ProfileFragmentDirections
import com.rino.self_services.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComplaintsFragment : Fragment() {
    val viewModel: ComplaintsViewModel by viewModels()
    private lateinit var binding: FragmentComplaintsBinding
    private var from_where = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from_where =  arguments?.get("from_where").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentComplaintsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        handleBack()

    }

    private fun handleBack() {
        binding.backbtn.setOnClickListener {

            if (from_where == "hr_profile") {
                val action =
                    ComplaintsFragmentDirections.actionComplaintsFragmentToProfileFragment("hr")
                findNavController().navigate(action)
            } else if (from_where == "payment_profile") {
                val action =
                    ComplaintsFragmentDirections.actionComplaintsFragmentToProfileFragment("payment")
                findNavController().navigate(action)
            } else if (from_where == "hr_view_complaints") {
                val action =
                    ComplaintsFragmentDirections.actionComplaintsFragmentToViewComplaintsFragment("hr_profile")
                findNavController().navigate(action)
            } else if (from_where == "payment_view_complaints") {
                val action =
                    ComplaintsFragmentDirections.actionComplaintsFragmentToViewComplaintsFragment("payment_profile")
                findNavController().navigate(action)
            }

        }
    }


}