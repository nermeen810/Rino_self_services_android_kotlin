package com.rino.self_services.ui.hrClearanceDetails

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentHrClearanceDetailsBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.ui.payment_process_details.PaymentProcessDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HRClearanceDetailsFragment : Fragment() {
    val viewModel: HRClearanceDetailsViewModel by viewModels()
    private lateinit var binding: FragmentHrClearanceDetailsBinding
    private lateinit var hrClearanceDetailsRequest: HRClearanceDetailsRequest
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
        binding.addAttachment.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            activity?.startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
        viewModel.getData(hrClearanceDetailsRequest)

        return binding.root
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
            if(it != null && it != ""){

//                binding.ppErrorMessage.text = it
//                binding.ppErrorMessage.visibility = View.VISIBLE
            }else{
//                binding.ppErrorMessage.visibility = View.GONE
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
                0 ->{ binding.clearanceStepper.setImageResource(R.drawable.first_stepper) }
                1 ->{ binding.clearanceStepper.setImageResource(R.drawable.second_stepper) }
                2 ->{ binding.clearanceStepper.setImageResource(R.drawable.third_stepper) }
                3 ->{ binding.clearanceStepper.setImageResource(R.drawable.fourth_stepper) }
                4 ->{ binding.clearanceStepper.setImageResource(R.drawable.fifth_stepper) }
                5 ->{ binding.clearanceStepper.setImageResource(R.drawable.sixth_stepper) }
                6 ->{ binding.clearanceStepper.setImageResource(R.drawable.seventh_stepper) }
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
//                binding.hrStart.text = details?.start ?: "لا يوجد"
//                binding.hrEnd.text = details?.end ?: "لا يوجد"
                binding.hrStart.alpha = 0f
                binding.hrEnd.alpha = 0f
            }
            if(details.status?.contains("جديد") == true){
                binding.hrApprove.alpha = 1f
                binding.hrDenay.alpha = 1f
            }else{
                binding.hrApprove.alpha = 0f
                binding.hrDenay.alpha = 0f
            }

        }
    }

}