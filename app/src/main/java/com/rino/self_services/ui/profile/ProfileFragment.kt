package com.rino.self_services.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rino.self_services.databinding.FragmentProfileBinding
import com.rino.self_services.ui.notifications.NotificationsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding
    var from_where =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from_where =  arguments?.get("from_where").toString()        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.logoutCard.setOnClickListener {
            Log.e("navToLogin","profile")
            viewModel.logout()
            navToLogin()
        }
        binding.backbtn.setOnClickListener {

            handleBack()
        }
    }

    private fun handleBack() {

            if (from_where == "hr") {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToHrClearanceHomeFragment()
                findNavController().navigate(action)
            } else if (from_where == "payment") {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToPaymentProcessesFragment()
                findNavController().navigate(action)
            }

    }

    private fun navToLogin() {
    //    Log.e("navToLogin","profile")
        val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
        findNavController().navigate(action)
    }


}