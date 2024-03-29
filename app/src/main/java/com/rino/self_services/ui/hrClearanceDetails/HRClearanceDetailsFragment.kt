package com.rino.self_services.ui.hrClearanceDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentHrClearanceDetailsBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.model.pojo.NavToAttachment
import com.rino.self_services.ui.main.FileCaller
import com.rino.self_services.ui.main.MainActivity
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.dateToArabic
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HRClearanceDetailsFragment : Fragment() {
    val viewModel: HRClearanceDetailsViewModel by viewModels()
    private lateinit var binding: FragmentHrClearanceDetailsBinding
    private lateinit var hrClearanceDetailsRequest: HRClearanceDetailsRequest
    private  var parts:ArrayList<MultipartBody.Part> = arrayListOf()
    var shouldShowActions = true
    lateinit var seeAll: NavSeeAll
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hrClearanceDetailsRequest =  arguments?.get("hr_clearance_details") as HRClearanceDetailsRequest
            shouldShowActions = hrClearanceDetailsRequest.isActionBefore
            seeAll = arguments?.get("nav_to_see_all_clearance") as NavSeeAll

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHrClearanceDetailsBinding.inflate(inflater, container, false)
        observeLoading()
        obseveError()
        oberveData()
        handleBackBotton()
        viewModel.getData(hrClearanceDetailsRequest)

        if (!shouldShowActions){
            binding.hrApprove.visibility = View.GONE
//            binding.approve.visibility = View.GONE
//            binding.deny.visibility = View.GONE
        }
        binding.hrApprove.setOnClickListener {
        viewModel.createAction(hrClearanceDetailsRequest.requestID,"approve",hrClearanceDetailsRequest.entity)
        }
        binding.hrDenay.visibility = View.GONE

        if (hrClearanceDetailsRequest.meOrOthers != "me"){
            binding.hrCreateAttachment.visibility = View.GONE
        }else{
            binding.hrCreateAttachment.visibility = View.VISIBLE
        }

        binding.hrCreateAttachment.setOnClickListener {
            (activity as MainActivity).caller = FileCaller.hrDetails
            (activity as MainActivity).openGalary()
            (activity as MainActivity).hrdetailsFile.observe(viewLifecycleOwner){
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
                    viewModel.createAttachment(parts, id = hrClearanceDetailsRequest.requestID, entity = 1)
                    (activity as MainActivity).caller = FileCaller.none
                    (activity as MainActivity)._hrDetailsFiles.value = ArrayList()

                }




            }
        }
        viewModel.isTrue.observe(viewLifecycleOwner){
            if (it){
                showMessage("تم اضافه المرفقات بنجاح")
                binding.viewAttachment.visibility = View.VISIBLE
                shouldShowActions = false
            }else{
                showMessage("حدث خطأً الرجاء المحاوله في وقت لاحق")
            }
        }
        return binding.root
    }


    private fun handleBackBotton() {
        binding.backbtn.setOnClickListener {
            if(seeAll.me_or_others==""&&seeAll.startPeriod==""&&seeAll.endPeriod=="")
            {
                val action =
                    HRClearanceDetailsFragmentDirections.hRClearanceDetailsFragmentToHrHome()
                findNavController().navigate(action)
            }
            else{
                val action =
                    HRClearanceDetailsFragmentDirections.hRClearanceDetailsFragmentToHrSeeAll(seeAll)
                findNavController().navigate(action)
            }

        }
    }


    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
            //    binding.clearanceDetailsProgress.visibility = it
                if(it == View.VISIBLE)
                {
                    Log.e("shimmer","start")
                    binding.shimmer.visibility = View.VISIBLE
                    binding.detailsLayout.visibility = View.GONE
                    binding.shimmer.startShimmer()

                }
                else{
                    Log.e("shimmer","stop")
                    binding.shimmer.stopShimmer()
                    binding.detailsLayout.visibility = View.VISIBLE
                    binding.shimmer.visibility = View.GONE

                }
            }
        }
    }

    private fun obseveError(){
        viewModel.setError.observe(viewLifecycleOwner){
            showMessage(it)
        }
    }
    private fun showMessage(msg: String) {
        lifecycleScope.launchWhenResumed {
            Snackbar.make(requireView(), msg, Snackbar.LENGTH_INDEFINITE)
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
    private fun oberveData(){
        viewModel.detailsData.observe(viewLifecycleOwner){
            var details = it.data
            binding.clearanceId.text = Constants.convertNumsToArabic(details.id.toString())
            binding.clearanceDetailsDate.text = details.date?.split("T")?.get(0)?.dateToArabic()
            binding.clearanceDescription.text = details.current?.name
            binding.hrDEmpName.text = details.employee
            binding.hrDEmpId.text = Constants.convertNumsToArabic(details.employeeID.toString())
            binding.hrDDepartment.text = details.department
            if(details.current?.users?.size !=0 && details.current?.users!= null) {
                binding.hrFowrwerdTo.text = details.current?.users?.get(0) ?: ""
            }
            else {
                binding.hrFowrwerdTo.text = details.current?.users?.get(0) ?: ""
                binding.textView39.visibility = View.GONE

            }
            binding.hrFowrwerdTo.text = details.current?.users?.get(0) ?: ""
            binding.hrType.text = details.type
            binding.hrDStatus.text = details.status
            if(details.status=="جديد"){
                binding.hrDenay.visibility =View.VISIBLE
            }
            else{
                binding.hrDenay.visibility =View.GONE
            }

            when(details.step){
                1 ->{ binding.clearanceStepper.setImageResource(R.drawable.second_stepper) }
                2 ->{ binding.clearanceStepper.setImageResource(R.drawable.third_stepper) }
                3 ->{ binding.clearanceStepper.setImageResource(R.drawable.fourth_stepper) }
                4 ->{ binding.clearanceStepper.setImageResource(R.drawable.fifth_stepper) }
                5 ->{ binding.clearanceStepper.setImageResource(R.drawable.sixth_stepper) }
                6 ->{ binding.clearanceStepper.setImageResource(R.drawable.seventh_stepper) }
                7 ->{ binding.clearanceStepper.setImageResource(R.drawable.seventh_stepper) }
            }
            if(details.type?.contains("خروج وعوده") == true && details.start != null && details.end != null){
                binding.hrLEnd.visibility = View.VISIBLE
                binding.hrLStart.visibility = View.VISIBLE
                binding.hrStart.text = details.start
                binding.hrEnd.text = details.end
                binding.hrStart.visibility = View.VISIBLE
                binding.hrEnd.visibility = View.VISIBLE
            }else{
                binding.hrLEnd.visibility = View.GONE
                binding.hrLStart.visibility = View.GONE
                binding.hrStart.visibility = View.GONE
                binding.hrEnd.visibility = View.GONE
            }
            if(details.hasApproved == false && details.hasPermission == true){
                binding.hrDenay.visibility = View.VISIBLE
                binding.hrApprove.visibility = View.VISIBLE
            }else{
                binding.hrDenay.visibility = View.GONE
                binding.hrApprove.visibility = View.GONE
            }
                binding.viewAttachment.setOnClickListener {
                    if(viewModel.attachments.size != 0) {
                        val action =
                            HRClearanceDetailsFragmentDirections.actionHRClearanceDetailsFragmentToPPAttachmentFragment(
                                NavToAttachment(
                                    hrClearanceDetailsRequest.entity,
                                    viewModel.attachments.toList().toTypedArray(),
                                    false,
                                    hrClearanceDetailsRequest.meOrOthers,
                                    hrClearanceDetailsRequest.requestID,
                                    shouldShowActions
                                ), seeAll
                            )
                        findNavController().navigate(action)
                    }
                    else{
                        showMessage("لا توجد مرفقات لهذا الطلب")
                    }
                }

            if (shouldShowActions){
                binding.hrApprove.visibility = View.VISIBLE
            }else{
                binding.hrApprove.visibility = View.GONE
            }

        }
    }
}