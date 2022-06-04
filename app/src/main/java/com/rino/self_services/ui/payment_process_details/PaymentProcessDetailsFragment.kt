package com.rino.self_services.ui.payment_process_details

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.system.Os.accept
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPaymentProcessDetailsBinding
import com.rino.self_services.databinding.FragmentPaymentProcessesBinding
import com.rino.self_services.databinding.FragmentSeeAllPaymentProcessBinding
import com.rino.self_services.model.pojo.AttachmentPayment
import com.rino.self_services.model.pojo.AttachmentResponse
import com.rino.self_services.model.pojo.SeeAllRequest
import com.rino.self_services.ui.main.MainActivity
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.seeAllPayment.SeeAllPaymentProcessViewModel
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
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PaymentProcessDetailsFragment : Fragment() {
    private lateinit var part:MultipartBody.Part
    private var action = ""
    val viewModel: PaymentProcessDetailsViewModel by viewModels()
    private lateinit var binding: FragmentPaymentProcessDetailsBinding
    private  var array:ArrayList<AttachmentPayment> = ArrayList()
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
        (activity as MainActivity).detailsData.observe(viewLifecycleOwner){
            setDuringImage(it)
        }
        binding.viewPpAttachments.setOnClickListener {

            var action = PaymentProcessDetailsFragmentDirections.actionPaymentProcessDetailsFragmentToPPAttachmentFragment(array.toList().toTypedArray())
            findNavController().navigate(action)
        }
        binding.addAttachmentPp.setOnClickListener {

//            if(action.isEmpty()){
//                showMessage("من فضلك ادخل حاله الطلب رفض او قبول")
//            }else{
//                (activity as MainActivity).openGalary()
//            }

        }
        viewModel.setToTrue.observe(viewLifecycleOwner){
            if (it){
                showMessage("تم اضافه المرفقات بنجاح")
            }
        }
        binding.deny.setOnClickListener {

        }
        binding.approve.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog__Center)

                .setTitle(resources.getString(R.string.approve_request_number)+requestId.toString())
//                .setMessage(resources.getString(R.string.add_attachment))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which -> }
                .setNegativeButton(resources.getString(R.string.approve_without_attachments)) { dialog, which ->
                    action = "deny"
                    viewModel.createAttachment(null,requestId,action)
                }
                .setPositiveButton(resources.getString(R.string.approve_with_attachments)) { dialog, which ->
                    action = "approve"
                    (activity as MainActivity).openGalary()

                }
                .show()

        }
        binding.deny.setOnClickListener {
            action = "deny"

        }

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
                1 ->{ binding.stepperView.setImageResource(R.drawable.first_stepper) }
                2 ->{ binding.stepperView.setImageResource(R.drawable.second_stepper) }
                3 ->{ binding.stepperView.setImageResource(R.drawable.third_stepper) }
                4 ->{ binding.stepperView.setImageResource(R.drawable.fourth_stepper) }
                5 ->{ binding.stepperView.setImageResource(R.drawable.fifth_stepper) }
                6 ->{ binding.stepperView.setImageResource(R.drawable.sixth_stepper) }
                7 ->{ binding.stepperView.setImageResource(R.drawable.seventh_stepper) }
            }
//            if(details?.status =="جديد"){
                binding.deny.alpha = 1f
//            }else{
//                binding.deny.alpha = 0f
//            }

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

                viewModel.createAttachment(part,requestId,action)
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
}