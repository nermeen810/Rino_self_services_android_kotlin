package com.rino.self_services.ui.payment_process_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPaymentProcessDetailsBinding
import com.rino.self_services.databinding.FragmentPaymentProcessesBinding
import com.rino.self_services.databinding.FragmentSeeAllPaymentProcessBinding
import com.rino.self_services.ui.seeAllPayment.SeeAllPaymentProcessViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentProcessDetailsFragment : Fragment() {
    val viewModel: PaymentProcessDetailsViewModel by viewModels()
    private lateinit var binding: FragmentPaymentProcessDetailsBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentProcessDetailsBinding.inflate(inflater, container, false)
        observeLoading()
        oberveData()
        obseveError()
        viewModel.getData(19714)

        return binding.root
    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.ppDetailsProgress.visibility = it
            }
        }
    }
    private fun obseveError(){
        viewModel.setError.observe(viewLifecycleOwner){
        if(it != null && it != ""){
            binding.ppErrorMessage.text = it
            binding.ppErrorMessage.visibility = View.VISIBLE
        }else{
            binding.ppErrorMessage.visibility = View.GONE
        }
        }
    }
    private fun oberveData(){
        viewModel.detailsData.observe(viewLifecycleOwner){
            var details = it.data
            binding.orderNumberDetails.text = details?.id.toString()
            binding.orderDateDetails.text = details?.date
            binding.orderState.text = details?.status
            binding.orderDescriptionPayment.text = details?.desc
            binding.beneficiaryPayment.text = details?.beneficiary
            binding.provisionPaymentDetails.text = details?.provision
            binding.paymentMethodPaymentprocessDetails.text = details?.paymentMethod
            binding.paymentProcessDetailsAmount.text = details?.amount.toString()
            if(details?.limit == null){
                binding.paymentLimit.text = "لا يوجد"
            }else{
                binding.paymentLimit.text = details.limit.toString()
            }
            when(details?.step){
                0 ->{ binding.stepperView.setImageResource(R.drawable.first_stepper) }
                1 ->{ binding.stepperView.setImageResource(R.drawable.second_stepper) }
                2 ->{ binding.stepperView.setImageResource(R.drawable.third_stepper) }
                3 ->{ binding.stepperView.setImageResource(R.drawable.fourth_stepper) }
                4 ->{ binding.stepperView.setImageResource(R.drawable.fifth_stepper) }
                5 ->{ binding.stepperView.setImageResource(R.drawable.sixth_stepper) }
                6 ->{ binding.stepperView.setImageResource(R.drawable.seventh_stepper) }
            }

        }
    }
}