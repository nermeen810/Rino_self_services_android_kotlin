package com.rino.self_services.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentHomeBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var navToDetails:NavToDetails? = null
    private var navToHr:HRClearanceDetailsRequest? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navToDetails = arguments?.get("nav_to_pp_details") as NavToDetails?
        navToHr = arguments?.get("nav_to_HR_details") as HRClearanceDetailsRequest?
        Log.e("errrrrorrrr no",navToDetails?.id.toString())
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        observePermission()
        observeShowError()
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
        binding.notificationBtn.setOnClickListener{
          navToManagementsAlerts()
        }

    }

    private fun navToManagementsAlerts() {
        val action = HomeFragmentDirections.actionHomeFragmentToManagementAlertsFragment()
        findNavController().navigate(action)
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
            NavToDetails("me",navToDetails!!.id,true), NavSeeAll
                ("","","")
        )
        findNavController().navigate(action)
//        action
    }
    private fun navToHRDetails(){
        val action = HomeFragmentDirections.actionHomeFragmentToHRClearanceDetailsFragment(
            HRClearanceDetailsRequest(navToHr!!.entity,navToHr!!.requestID,true,"me"),
            NavSeeAll("","","")
        )
        findNavController().navigate(action)
    }
    private fun observePermission() {
        viewModel.getPermissions()
        viewModel.getPermission.observe(viewLifecycleOwner){
            it.let {
                if(it?.isGM==true)
                {
                    binding.notificationBtn.visibility = View.VISIBLE
                }
                else{
                    binding.notificationBtn.visibility = View.GONE                }
            }
        }
    }
    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_orange)).setActionTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white)).setAction(getString(R.string.dismiss))
                    {
                    }.show()
            }
        }
    }
}