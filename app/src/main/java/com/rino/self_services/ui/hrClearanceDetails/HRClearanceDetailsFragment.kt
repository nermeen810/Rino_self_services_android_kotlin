package com.rino.self_services.ui.hrClearanceDetails

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentHrClearanceDetailsBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.ui.main.MainActivity
import com.rino.self_services.ui.payment_process_details.PaymentProcessDetailsViewModel
import com.rino.self_services.ui.seeAllHr.SeeAllHrClearanceFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class HRClearanceDetailsFragment : Fragment() {
    val viewModel: HRClearanceDetailsViewModel by viewModels()
    private lateinit var binding: FragmentHrClearanceDetailsBinding
    private lateinit var hrClearanceDetailsRequest: HRClearanceDetailsRequest
    private lateinit var part:MultipartBody.Part
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
        navToViewAttachments()
        binding.addAttachment.setOnClickListener {
            (activity as MainActivity).openGalary()

        }
        (activity as MainActivity).detailsData.observe(viewLifecycleOwner){
            setDuringImage(it)
//            viewModel.createAttachment(part)
        }
//        main.detailsData.observe(viewLifecycleOwner){
////            bitmap = it
//
//
//        }
        viewModel.getData(hrClearanceDetailsRequest)

        return binding.root
    }


    private fun navToViewAttachments() {

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
    private fun setDuringImage(bitmap: Bitmap) {
        var duringBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        CoroutineScope(Dispatchers.Default).launch {

            duringBitmap = duringBitmap

            try {
                val file =
                    File(getRealPathFromURI(getImageUri(requireContext(), duringBitmap,"Attachments")))
                println("duringFilePath" + file.path)
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                part = MultipartBody.Part.createFormData(
                    "Attachments",
                    file.name.trim(),
                    requestFile
                )

                viewModel.createAttachment(part,hrClearanceDetailsRequest.entity,hrClearanceDetailsRequest.requestID)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
    fun getRealPathFromURI(uri: Uri?): String? {
        val cursor: Cursor? =
            uri?.let { requireActivity().getContentResolver().query(it, null, null, null, null) }
        cursor?.moveToFirst()
        val idx: Int? = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return idx?.let { cursor.getString(it) }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap, title:String): Uri? {
        val bytes = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            inImage.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 20, bytes)
        }
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            title+ currentDate.toString().replace(" ",""),
            null
        )

        return Uri.parse(path)
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
        observeAction()
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
                binding.hrApprove.setOnClickListener{

                    binding.hrApprove.alpha = 0f
                    viewModel.actionApproveOrDeny(details?.entity,details?.id,"approve")
                }
                binding.hrDenay.setOnClickListener{
                    binding.hrDenay.alpha = 0f
                    viewModel.actionApproveOrDeny(details?.entity,details?.id,"deny")

                }
            }else{
                binding.hrApprove.alpha = 1f
                binding.hrDenay.alpha = 0f
                binding.hrApprove.setOnClickListener{
                    binding.hrApprove.alpha = 0f
                    viewModel.actionApproveOrDeny(details?.entity,details?.id,"approve")

                }
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

    private fun observeAction() {
        viewModel.action.observe(viewLifecycleOwner){
            var msg = ""
             if(it=="approve"){
                msg = getString(R.string.approve_msg)
            }
            else if(it=="deny"){
                  msg = getString(R.string.deny_msg)
             }
            Toast.makeText(
                requireActivity(),
               msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}