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
        navToMyProfile()
        navToChangePassword()
        navToComplaints()
        logout()
        handleBack()
        observePermission()
    }

    private fun navToChangePassword() {
        binding.changePasswordBtn.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment(from_where)
            findNavController().navigate(action)
        }
        binding.changePasswordTxt.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment(from_where)
            findNavController().navigate(action)
        }
    }

    private  fun navToMyProfile(){
         binding.editProfileBtn.setOnClickListener {
             val action =
                 ProfileFragmentDirections.actionProfileFragmentToMyProfileFragment(from_where)
             findNavController().navigate(action)
        }
        binding.editProfileTxt.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToMyProfileFragment(from_where)
            findNavController().navigate(action)
        }
    }

    private fun navToComplaints() {
        binding.complaintsBtn.setOnClickListener {
            viewModel.getPermissions()
        }
        binding.complaintsTxt.setOnClickListener {
            viewModel.getPermissions()
        }
    }

    private fun observePermission() {
        viewModel.getPermission.observe(viewLifecycleOwner){
            it.let {
                if(it?.isDepartmentHead==true||it?.isGM==true)
                {
                    navToViewComplaints()
                }
                else{
                    navToCreateComplaints()
                }
            }
        }
    }

    private fun navToCreateComplaints() {
        val action =
            ProfileFragmentDirections.actionProfileFragmentToComplaintsFragment(from_where+"_profile")
        findNavController().navigate(action)
    }

    private fun navToViewComplaints() {
        val action =
            ProfileFragmentDirections.actionProfileFragmentToViewComplaintsFragment(from_where+"_profile")
        findNavController().navigate(action)
    }

    private fun logout() {
        binding.logoutBtn.setOnClickListener {
            viewModel.logout()
            navToLogin()
        }
        binding.logoutTxt.setOnClickListener {
            Log.e("navToLogin","profile")
            viewModel.logout()
            navToLogin()
        }
    }

    private fun handleBack() {
        binding.backbtn.setOnClickListener {

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
    }

    private fun navToLogin() {
    //    Log.e("navToLogin","profile")
        val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
        findNavController().navigate(action)
    }


}