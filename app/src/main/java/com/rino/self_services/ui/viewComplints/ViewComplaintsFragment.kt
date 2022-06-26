package com.rino.self_services.ui.viewComplints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rino.self_services.databinding.FragmentViewComplaintsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewComplaintsFragment : Fragment() {
    val viewModel: ViewComplaintsViewModel by viewModels()
    private lateinit var binding: FragmentViewComplaintsBinding
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
        // Inflate the layout for this fragment
        binding = FragmentViewComplaintsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        handleBack()
        addComplaint()
    }

    private  fun addComplaint(){
        binding.fab.setOnClickListener{
            if (from_where == "hr_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToComplaintsFragment("hr_view_complaints")
                findNavController().navigate(action)
            } else if (from_where == "payment_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToComplaintsFragment("payment_view_complaints")
                findNavController().navigate(action)
            }
        }
    }

    private fun handleBack() {
        binding.backbtn.setOnClickListener {

            if (from_where == "hr_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToProfileFragment("hr")
                findNavController().navigate(action)
            } else if (from_where == "payment_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToProfileFragment("payment")
                findNavController().navigate(action)
            }
        }
    }
}