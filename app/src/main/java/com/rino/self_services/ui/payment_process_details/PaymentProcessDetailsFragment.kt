package com.rino.self_services.ui.payment_process_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPaymentProcessDetailsBinding
import com.rino.self_services.databinding.FragmentPaymentProcessesBinding
import com.rino.self_services.databinding.FragmentSeeAllPaymentProcessBinding
import com.rino.self_services.model.pojo.SeeAllRequest
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.seeAllPayment.SeeAllPaymentProcessViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentProcessDetailsFragment : Fragment() {
    val viewModel: PaymentProcessDetailsViewModel by viewModels()
    private lateinit var binding: FragmentPaymentProcessDetailsBinding
     var requestId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            requestId = arguments?.get("id") as Int
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentProcessDetailsBinding.inflate(inflater, container, false)
        observeLoading()
        oberveData()
        obseveError()
        viewModel.getData(requestId)
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
            showMessage(it)
//            binding.ppErrorMessage.text = it
//            binding.ppErrorMessage.visibility = View.VISIBLE
        }else{
            binding.ppErrorMessage.visibility = View.GONE
        }
        }
    }
    private fun showMessage(msg: String) {
        lifecycleScope.launchWhenResumed {
            Snackbar.make(requireView(), msg, Snackbar.LENGTH_INDEFINITE)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                    resources.getColor(
                        R.color.color_orange
                    )
                )
                .setActionTextColor(resources.getColor(R.color.white))
                .setAction(getString(R.string.dismiss))
                {
                }.show()
        }
    }
    private fun oberveData(){
        viewModel.detailsData.observe(viewLifecycleOwner){
            var details = it.data
            binding.orderNumberDetails.text = details?.id.toString()
            binding.orderDateDetails.text = details?.date?.split("T")?.get(0)
            binding.orderState.text = details?.status
            binding.orderDescriptionPayment.text = details?.desc
            binding.beneficiaryPayment.text = details?.beneficiary
            binding.provisionPaymentDetails.text = details?.provision
            binding.paymentMethodPaymentprocessDetails.text = details?.payType
            binding.paymentProcessDetailsAmount.text = details?.amount.toString()
            binding.orderSidePayment.text = details?.department
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
            if(details?.status =="جديد"){
                binding.deny.alpha = 1f
            }else{
                binding.deny.alpha = 0f
            }

        }
    }
}