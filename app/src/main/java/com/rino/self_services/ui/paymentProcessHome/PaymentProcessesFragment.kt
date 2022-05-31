package com.rino.self_services.ui.paymentProcessHome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentLoginBinding
import com.rino.self_services.databinding.FragmentPaymentProcessesBinding
import com.rino.self_services.ui.login.LoginFragmentDirections


class PaymentProcessesFragment : Fragment() {
    private lateinit var binding: FragmentPaymentProcessesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentProcessesBinding.inflate(inflater, container, false)
        binding.navigateToSeeall.setOnClickListener {
            navigateToHome()
        }
        return  binding.root
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_payment_processes, container, false)
    }
    private fun navigateToHome() {
        val action = PaymentProcessesFragmentDirections.paymentProcessToSeeAll()
        findNavController().navigate(action)
    }


}