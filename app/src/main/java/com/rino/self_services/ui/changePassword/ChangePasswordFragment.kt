package com.rino.self_services.ui.changePassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    val viewModel: ChangePasswordViewModel by viewModels()
    private lateinit var binding: FragmentChangePasswordBinding
    private var oldPass = ""
    private var newPass = ""
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
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        backBtnOnClick()
        saveNewPassOnClick()
        observeData()
    }
    private fun saveNewPassOnClick() {
        binding.saveButton.setOnClickListener {
            getDataFromUI()
            validateData()
        }
    }

    private fun getDataFromUI() {
        oldPass = binding.editTextOldPassword.text.toString()
        newPass = binding.editTextNewPassword.text.toString()

    }

    private fun validateData() {
        validateOLdPassword()
        validateNewPassword()
        if (validateOLdPassword() && validateNewPassword()) {
            viewModel.changePassword(oldPass, newPass)
        }

    }

    private fun validateNewPassword() :Boolean{
        val passwordVal = "^" + "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                //   "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{6,}"  //at least 8 characters
//                "$"
       return  if (newPass.isEmpty()) {
                binding.textInputNewPassword.error = getString(R.string.required_field)
                false
            } else if (newPass == oldPass) {
                //  Toast.makeText(requireActivity(), "newPass :" + newPass == newPassCongirm, Toast.LENGTH_SHORT).show()
                binding.textInputNewPassword.error = getString(R.string.oldPassword_and_newPassword_are_the_same)
               binding.textInputOldPassword.error = getString(R.string.oldPassword_and_newPassword_are_the_same)
                false
            } else if (!newPass.matches(passwordVal.toRegex())) {
           binding.textInputNewPassword.error = getString(R.string.weak_password)
           false
       } else {
                binding.textInputNewPassword.error = null
                binding.textInputNewPassword.isErrorEnabled = false
                true
            }
    }

    private fun validateOLdPassword(): Boolean {
        return if (oldPass.isEmpty()) {
            binding.textInputOldPassword.error = getString(R.string.required_field)
            false
        }  else {
            binding.textInputOldPassword.error = null
            binding.textInputOldPassword.isErrorEnabled = false
            true
        }

    }

    private fun backBtnOnClick() {
        binding.backbtn.setOnClickListener {
            navToProfile()
        }
    }

    private fun navToProfile() {
        val action =
            ChangePasswordFragmentDirections.actionChangePasswordFragmentToProfileFragment(from_where)
        findNavController().navigate(action)
    }

    private fun observeData() {
        observeSuccessResetPass()
        observeLoading()
        observeShowError()
    }
    private fun observeSuccessResetPass() {
        viewModel.changePasswordResponse.observe(viewLifecycleOwner) {
            it.let {
                Toast.makeText(
                    requireActivity(),
                   "تم تغيير كلمة المرور بنجاح",
                    Toast.LENGTH_SHORT
                ).show()
                navToProfile()

            }
        }
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.progress.visibility = it
            }
        }
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(ContextCompat.getColor(
                        requireContext(),
                        R.color.color_orange
                    )
                    )
                    .setActionTextColor(resources.getColor(R.color.white)).setAction("Ok")
                    {
                    }.show()
            }
        }
    }


}