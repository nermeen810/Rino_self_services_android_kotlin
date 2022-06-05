package com.rino.self_services.ui.hrClearanceDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentHrClearanceDetailsBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HRClearanceDetailsFragment : Fragment() {
    val viewModel: HRClearanceDetailsViewModel by viewModels()
    private lateinit var binding: FragmentHrClearanceDetailsBinding
    private lateinit var hrClearanceDetailsRequest: HRClearanceDetailsRequest
    private  var parts:ArrayList<MultipartBody.Part?> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hrClearanceDetailsRequest =  arguments?.get("hr_clearance_details") as HRClearanceDetailsRequest
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
        binding.hrApprove.setOnClickListener {

        }
        binding.hrDenay.setOnClickListener {

        }
        viewModel.getData(hrClearanceDetailsRequest)

        return binding.root
    }


    private fun handleBackBotton() {
        binding.backbtn.setOnClickListener {
            val action =
                HRClearanceDetailsFragmentDirections.hRClearanceDetailsFragmentToHrHome()
            findNavController().navigate(action)
        }
    }


    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.clearanceDetailsProgress.visibility = it
            }
        }
    }

    private fun obseveError(){
        viewModel.setError.observe(viewLifecycleOwner){
            showMessage(it)
//            if(it != null && it != ""){
//
////                binding.ppErrorMessage.text = it
////                binding.ppErrorMessage.visibility = View.VISIBLE
//            }else{
////                binding.ppErrorMessage.visibility = View.GONE
//            }
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
            binding.clearanceId.text = details?.id.toString()
            binding.clearanceDetailsDate.text = details?.date?.split("T")?.get(0)
            binding.clearanceDescription.text = details?.current?.name
            binding.hrDEmpName.text = details?.employee
            binding.hrDEmpId.text = details?.employeeID?.toString()
            binding.hrDDepartment.text = details?.department
            binding.hrFowrwerdTo.text = details?.current?.users?.get(0) ?: ""
            binding.hrType.text = details?.type
            binding.hrDStatus.text = details.status

            when(details?.step){
                1 ->{ binding.clearanceStepper.setImageResource(R.drawable.second_stepper) }
                2 ->{ binding.clearanceStepper.setImageResource(R.drawable.third_stepper) }
                3 ->{ binding.clearanceStepper.setImageResource(R.drawable.fourth_stepper) }
                4 ->{ binding.clearanceStepper.setImageResource(R.drawable.fifth_stepper) }
                5 ->{ binding.clearanceStepper.setImageResource(R.drawable.sixth_stepper) }
                6 ->{ binding.clearanceStepper.setImageResource(R.drawable.seventh_stepper) }
                7 ->{ binding.clearanceStepper.setImageResource(R.drawable.seventh_stepper) }
            }
            if(details.type?.contains("خروج وعوده") == true && details.start != null && details.end != null){
                binding.hrLEnd.alpha = 1f
                binding.hrLStart.alpha = 1f
                binding.hrStart.text = details?.start
                binding.hrEnd.text = details?.end
                binding.hrStart.alpha = 1f
                binding.hrEnd.alpha = 1f
            }else{
                binding.hrLEnd.alpha = 0f
                binding.hrLStart.alpha = 0f
                binding.hrStart.alpha = 0f
                binding.hrEnd.alpha = 0f
            }
            if(details.hasApproved == false && details.hasPermission == true){
                binding.hrDenay.alpha = 1f
                binding.hrApprove.alpha = 1f
            }else{
                binding.hrDenay.alpha = 0f
                binding.hrApprove.alpha = 0f
            }

            if(details.attachment.size==0)
            {
                binding.viewAttachment.visibility =View.GONE
            }
            else {
                binding.viewAttachment.setOnClickListener {
                    val action =
                        HRClearanceDetailsFragmentDirections.hrClearanceDetailsToHrViewAttachments(
                            details.attachment,
                            hrClearanceDetailsRequest
                        )
                    findNavController().navigate(action)
                }
            }

        }
    }

//    private fun observeAction() {
//        viewModel.action.observe(viewLifecycleOwner){
//            var msg = ""
//             if(it=="approve"){
//                msg = getString(R.string.approve_msg)
//            }
//            else if(it=="deny"){
//                  msg = getString(R.string.deny_msg)
//             }
//            Toast.makeText(
//                requireActivity(),
//               msg,
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

}