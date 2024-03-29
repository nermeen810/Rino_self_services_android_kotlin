package com.rino.self_services.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentLoginBinding
import com.rino.self_services.model.pojo.login.LoginRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    val viewModel: LoginViewModel  by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private var email = ""
    private var pass = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        loginButtonOnClick()
            resetPassOnClick()
        privacyPolicyOnClick()
        observeData()
    }

    private fun privacyPolicyOnClick() {
        val text = getString(R.string.privacy_policy)
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, text.length, 0)
        binding.privacyPolicyTxt.text = content
        binding.privacyPolicyTxt.setOnClickListener {
            navtoPrivacyPolicyUrl()
        }
    }

    private fun navtoPrivacyPolicyUrl() {
        val url = "https://amanat-jeddah-staging.azurewebsites.net/Home/PrivacyPolicy"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun loginButtonOnClick() {
        binding.loginButton.setOnClickListener {
            //   binding.progress.visibility = View.VISIBLE
            email = binding.editTextEmail.text.toString()
            pass = binding.editTextPassword.text.toString()
            validateData()
        }
    }

    private fun resetPassOnClick() {
        binding.resetPassTxt.setOnClickListener {
            navigateToResetPass()
        }
    }

    private fun navigateToResetPass() {
        val action = LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment()
        findNavController().navigate(action)
    }

    private fun observeData() {
        observeSuccessLogin()
        observeLoading()
        observeShowError()

    }

    private fun observeSuccessLogin() {
        viewModel.isLogin.observe(viewLifecycleOwner) {
            if (it) {
                //   binding.progress.visibility = View.GONE
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.success_login),
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()

            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.invalid_email_or_pass),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToHome() {
        val action = LoginFragmentDirections.loginToHome()
        findNavController().navigate(action)
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
                showMessage(it)
            }
        }
    }

    private fun validateData() {
        validateEmail()
        validatPassword()
        if (validateEmail() && validatPassword()) {
            viewModel.login(LoginRequest(email, pass))
        }
    }

    private fun validateEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return if (email.isEmpty()) {
            binding.textInputEmail.error = getString(R.string.required_field)
            false
        } else if (email.length > 50) {
            binding.textInputEmail.error = getString(R.string.email_must_less_than50)
            false
        }
//        else if (!email.matches(emailPattern)) {
//            binding.textInputEmail.error = getString(R.string.invalid_email)
//            false
//        }
        else {
            binding.textInputEmail.error = null
            binding.textInputEmail.isErrorEnabled = false
            true
        }
    }

    private fun validatPassword(): Boolean {
        val passwordVal = "^" + "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                //   "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{8,}"  //at least 8 characters
//                "$"
        return if (pass.isEmpty()) {
            binding.textInputPassword.error = getString(R.string.required_field)
            false
        }
//        else if (!pass.matches(passwordVal.toRegex())) {
//            binding.textInputPassword.error = "كلمة المرور ضعيفة"
//            false
//        }
        else {
            binding.textInputPassword.error = null
            binding.textInputPassword.isErrorEnabled = false
            true
        }
    }

    override fun onResume() {
        super.onResume()
      //  (activity as MainActivity).bottomNavigation.isGone = true
    }

    private fun showMessage(msg: String) {
        lifecycleScope.launchWhenResumed {
            Snackbar.make(requireView(), msg, Snackbar.LENGTH_INDEFINITE)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_orange)).setActionTextColor(ContextCompat.getColor(
                    requireContext(),
                    R.color.white)).setAction(getString(R.string.dismiss))
                {
                }.show()
        }
    }

}