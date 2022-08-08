package com.rino.self_services.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentHomeBinding
import com.rino.self_services.databinding.FragmentLoginBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.ui.login.LoginFragmentDirections
import com.rino.self_services.ui.login.LoginViewModel
import com.rino.self_services.ui.main.MainActivity
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    var navToDetails:NavToDetails? = null
    private var navToHr:HRClearanceDetailsRequest? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navToDetails = arguments?.get("nav_to_pp_details") as NavToDetails?
        navToHr = arguments?.get("nav_to_HR_details") as HRClearanceDetailsRequest?
        Log.e("errrrrorrrr no",navToDetails?.id.toString())
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
        if(navToDetails != null){
            navToPaymentDetails()
        }
        if(navToHr != null){
            navToHRDetails()
        }
//        binding.notificationBtn.setOnClickListener{
//
//        }
    }
    private fun navigateToPayment() {
        val action = HomeFragmentDirections.homeToPayment()
        findNavController().navigate(action)
    }
    private fun navigateToHr() {
        val action = HomeFragmentDirections.homeToHr()
        findNavController().navigate(action)
    }
    private fun navToPaymentDetails(){
        val action = HomeFragmentDirections.actionHomeFragmentToPaymentProcessDetailsFragment(
            NavToDetails("me",navToDetails!!.id,false), NavSeeAll
                ("","","")
        )
        findNavController().navigate(action)
//        action
    }
    private fun navToHRDetails(){
        val action = HomeFragmentDirections.actionHomeFragmentToHRClearanceDetailsFragment(
            HRClearanceDetailsRequest(navToHr!!.entity,navToHr!!.requestID,false,"me"),
            NavSeeAll("","","")
        )
        findNavController().navigate(action)
    }

}