package com.rino.self_services.ui.payment_process_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPaymentProcessDetailsBinding
import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.ui.main.FileCaller
import com.rino.self_services.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PaymentProcessDetailsFragment : Fragment() {
    private  var parts = ArrayList<MultipartBody.Part>()
    private var action = ""
    val viewModel: PaymentProcessDetailsViewModel by viewModels()
    private lateinit var binding: FragmentPaymentProcessDetailsBinding
    private  var array:ArrayList<Attachment> = ArrayList()
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
        handleBackBotton()
        viewModel.getData(requestId)
        (activity as MainActivity).paymentProcessFiles.observe(viewLifecycleOwner){
            parts = ArrayList()
            it.map {
                val requestFile: RequestBody =
                    it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                var  part = MultipartBody.Part.createFormData(
                    "Attachments",
                    it.name.trim(),
                    requestFile
                )
                parts.add(part)
            }

            viewModel.createAttachment(parts,requestId,action)
        }
        binding.viewPpAttachments.setOnClickListener {

            var action = PaymentProcessDetailsFragmentDirections.actionPaymentProcessDetailsFragmentToPPAttachmentFragment(array.toList().toTypedArray())
            findNavController().navigate(action)
        }
        viewModel.setToTrue.observe(viewLifecycleOwner){
            if (it){
                showMessage("تم اضافه المرفقات بنجاح")
            }else{
                showMessage("حدث خطأً الرجاء المحاوله في وقت لاحق")
            }
        }
        binding.deny.setOnClickListener {
            action = "deny"

        }
        binding.approve.setOnClickListener {
            action = "approve"

        }
        binding.ppAddAttachment.setOnClickListener{
            (activity as MainActivity).caller = FileCaller.paymentDetails
            (activity as MainActivity).openGalary()
        }
        return binding.root
    }
    private fun handleBackBotton() {
        binding.backbtn.setOnClickListener {
            val action =
                PaymentProcessDetailsFragmentDirections.paymentProcessDetailsFragmentToPaymentHome()
            findNavController().navigate(action)
        }
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
            details?.attachments?.let { it1 -> array.addAll(it1) }
            Toast.makeText(requireContext(),"done",Toast.LENGTH_LONG).show()
            binding.orderNumberDetails.text = details?.id.toString()
            binding.orderDateDetails.text = details?.date?.split("T")?.get(0)
            binding.orderState.text = details?.status
            binding.orderDescriptionPayment.text = details?.desc
            binding.beneficiaryPayment.text = details?.beneficiary
            binding.provisionPaymentDetails.text = details?.provision
            binding.paymentMethodPaymentprocessDetails.text = details?.payType
            binding.paymentProcessDetailsAmount.text = details?.amount.toString()
            binding.orderSidePayment.text = details?.department
            binding.beneficiaryPayment.text = details?.beneficiary ?: "لا يوجد"
            binding.approve.text = details?.current?.name
            binding.paymentLimit.text = details?.limit?.toString() ?: "لا يوجد"

            when(details?.step){
                1 ->{ binding.stepperView.setImageResource(R.drawable.second_stepper) }
                2 ->{ binding.stepperView.setImageResource(R.drawable.third_stepper) }
                3 ->{ binding.stepperView.setImageResource(R.drawable.fourth_stepper) }
                4 ->{ binding.stepperView.setImageResource(R.drawable.fifth_stepper) }
                5 ->{ binding.stepperView.setImageResource(R.drawable.sixth_stepper) }
                6 ->{ binding.stepperView.setImageResource(R.drawable.seventh_stepper) }
                7 ->{ binding.stepperView.setImageResource(R.drawable.seventh_stepper) }
            }
                if(details?.hasApproved == false && details.hasPermission == true){
                    binding.deny.alpha = 1f
                    binding.approve.alpha = 1f
                }else{
                    binding.deny.alpha = 0f
                    binding.approve.alpha = 0f
                }
        }
    }
}