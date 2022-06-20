package com.rino.self_services.ui.payment_process_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPaymentProcessDetailsBinding
import com.rino.self_services.model.pojo.NavToAttachment
import com.rino.self_services.ui.main.FileCaller
import com.rino.self_services.ui.main.MainActivity
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PaymentProcessDetailsFragment : Fragment() {
    var shouldShowActions = true
    private  var parts = ArrayList<MultipartBody.Part>()
    private var action = ""
    val viewModel: PaymentProcessDetailsViewModel by viewModels()
    private lateinit var binding: FragmentPaymentProcessDetailsBinding
    private  lateinit var navToDetails: NavToDetails
    private  lateinit var seeAll:NavSeeAll
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navToDetails = arguments?.get("nav_to_pp_details") as NavToDetails
            seeAll = arguments?.get("nav_to_see_all_payment") as NavSeeAll
            shouldShowActions = navToDetails.isActionBefore

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
        viewModel.getData(navToDetails.id)
        if(navToDetails.me_or_others == "others"){
            binding.approve.visibility = View.GONE
        }
        if (!navToDetails.isActionBefore ){
            binding.approve.visibility = View.GONE
            binding.deny.visibility = View.GONE
        }


        binding.viewPpAttachments.setOnClickListener {

            var action = PaymentProcessDetailsFragmentDirections.actionPaymentProcessDetailsFragmentToPPAttachmentFragment(NavToAttachment(null,viewModel.attachments.toList().toTypedArray(),true,navToDetails.me_or_others,navToDetails.id,shouldShowActions),seeAll)
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
//            action = "deny"
//            viewModel.createAction(requestId,action)


        }
        binding.approve.setOnClickListener {
            action = "approve"
            viewModel.createAction(navToDetails.id,action)
        }
        if (navToDetails.me_or_others != "me"){
            binding.ppAddAttachment.visibility = View.GONE
        }else{
            binding.ppAddAttachment.visibility = View.VISIBLE
        }
        binding.ppAddAttachment.setOnClickListener{
            (activity as MainActivity).caller = FileCaller.paymentDetails
            (activity as MainActivity).openGalary()
            (activity as MainActivity).paymentProcessFiles.observe(viewLifecycleOwner){
                if (!it.isEmpty()){
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

                    viewModel.createAttachment(parts,navToDetails.id)
                    (activity as MainActivity)._paymentProcessFiles.value = ArrayList()
                }
            }
        }
        viewModel.action.observe(viewLifecycleOwner){
            if (it.success == true){
                shouldShowActions = false
                binding.approve.alpha = 0f
                binding.deny.alpha = 0f
                showMessage("تم تعديل حاله الطلب بنجاح")
                viewModel.getData(navToDetails.id)
            }else{
                it.message?.let { it1 -> showMessage(it1) }
            }
        }
        return binding.root
    }
    private fun handleBackBotton() {
        binding.backbtn.setOnClickListener {
            if(seeAll.me_or_others == ""&&seeAll.startPeriod ==""&&seeAll.endPeriod=="") {
                val action =
                    PaymentProcessDetailsFragmentDirections.paymentProcessDetailsFragmentToHomePayment()
                findNavController().navigate(action)
            }
            else{
         //       Log.e("seeAll",seeAll.me_or_others+" , "+seeAll.startPeriod+" , "+seeAll.endPeriod)
                PaymentProcessDetailsFragmentDirections.paymentProcessDetailsFragmentToSeeAllPayment(seeAll)
                findNavController().navigate(action)
            }
        }
    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.ppDetailsProgress.visibility = it
//                if( it == View.GONE){
//                    binding.deny.alpha = 0f
//                    binding.approve.alpha = 0f
//                }else{
//                    binding.deny.alpha = 0f
//                    binding.approve.alpha = 0f
//                }
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

//            if(details?.hasMadeAction == false && details?.status == "جديد"){
//                binding.deny.alpha = 1f
//                binding.approve.alpha = 1f
//            }else if(details?.hasMadeAction == true){
//                binding.deny.alpha = 0f
//                binding.approve.alpha = 0f
//            }else if(details?.hasMadeAction == false &&  details?.status == "تم اعتماد مدير القسم"){
//                binding.approve.alpha = 1f
//
//            }else if(details?.hasMadeAction == true &&  details?.status == "تم اعتماد مدير القسم"){
//                binding.deny.alpha = 0f
//                binding.approve.alpha = 0f
//            }else if(details?.status?.contains("تم اعتماد مدير الحسابات") == true && details?.hasMadeAction == true){
//                binding.deny.alpha = 0f
//                binding.approve.alpha = 0f
//            }else if(details?.status?.contains("اعتماد المراةع المالي") == true){}
        }
    }
}