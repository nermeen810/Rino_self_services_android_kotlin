package com.rino.self_services.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentHomeBinding
import com.rino.self_services.databinding.FragmentLoginBinding
import com.rino.self_services.ui.login.LoginFragmentDirections
import com.rino.self_services.ui.login.LoginViewModel
import com.rino.self_services.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        binding.hrClearanceBtn.setOnClickListener{
            navigateToHr()
        }
        binding.paymentProcessBtn.setOnClickListener{
            navigateToPayment()
        }
        binding.notificationBtn.setOnClickListener{

        }
    }
    private fun navigateToPayment() {
        val action = HomeFragmentDirections.homeToPayment()
        findNavController().navigate(action)
    }
    private fun navigateToHr() {
        val action = HomeFragmentDirections.homeToHr()
        findNavController().navigate(action)
    }


//    override fun onResume() {
//        super.onResume()
//        (activity as MainActivity).bottomNavigation.isGone = false
//    }
}