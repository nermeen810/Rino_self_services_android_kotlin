package com.rino.self_services.ui.complaints

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentComplaintsBinding
import com.rino.self_services.ui.main.FileCaller
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.dateToArabic
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.apache.commons.io.FileUtils
import java.io.File


@AndroidEntryPoint
class ComplaintsFragment : Fragment() {
    val viewModel: ComplaintsViewModel by viewModels()
    private lateinit var binding: FragmentComplaintsBinding
    private var from_where = ""
    private var department = ""
    private var isDepartmentSelected = false
    private  var parts:ArrayList<MultipartBody.Part> = arrayListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from_where =  arguments?.get("from_where").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentComplaintsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel.getDepartment()
        observeData()
        setBodyLength()
        addAttachments()
        addComplaint()
        handleBack()

    }

    @SuppressLint("SetTextI18n")
    private fun setBodyLength() {
        binding.notesEditTxt.addTextChangedListener {
            val notesLength =  binding.notesEditTxt.text.toString().length
            binding.notesLength.text =
                "${Constants.convertNumsToArabic(notesLength.toString())} ${getString(R.string.chars).dateToArabic()} "
        }

    }

    private fun addComplaint() {
        binding.addComplaintsBtn.setOnClickListener {
            Log.e("attachment",parts.toString())
            val body =   binding.notesEditTxt.text.toString()
            val officer = binding.administratorEditTxt.text.toString()
            if(validateData(officer,body)) {
                viewModel.createComplaint(department, officer, body, parts)
            }
            else{
                showMessage("برجاء اضافه البيانات المطلوبة")
            }
        }
    }

    private fun validateData(officer:String,body:String) :Boolean{
        var officerFlag = false
        var bodyFlag    = false
         if (officer.isEmpty()) {
            binding.textInputAdministrator.error = getString(R.string.required_field)
            officerFlag = false
        } else {
            binding.textInputAdministrator.error = null
            binding.textInputAdministrator.isErrorEnabled = false
             officerFlag =  true
        }
        if (body.isEmpty()) {
            binding.textInputNotes.error = getString(R.string.required_field)
            bodyFlag = false
        }
        if (body.length >500) {
            binding.textInputNotes.error = getString(R.string.must_be_less_than_500)
            bodyFlag = false
        }

        else {
            binding.textInputNotes.error = null
            binding.textInputNotes.isErrorEnabled = false
            bodyFlag =  true
        }
        if (!isDepartmentSelected) {
            binding.departmentTextInputLayout.error = getString(R.string.required_field)
        } else {
            binding.departmentTextInputLayout.error = null
            binding.departmentTextInputLayout.isErrorEnabled = false
        }
        return (officerFlag && bodyFlag && isDepartmentSelected)
    }

    private fun addAttachments() {
        binding.addAttachmentBtn.setOnClickListener {
            openGalary()
        }
    }

    private fun getImageFromUri(imageUri: Uri?) : File? {
        imageUri?.let { uri ->
            val mimeType = getMimeType(requireActivity(), uri)
            mimeType?.let {
                val file = createTmpFileFromUri(requireActivity(), imageUri,"temp_image", ".$it")
//                file?.let { Log.d("image Url = ", file.absolutePath) }
                return file
            }
        }
        return null
    }


    private fun getMimeType(context: Context, uri: Uri): String? {
        //Check uri format to avoid null
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    private fun createTmpFileFromUri(context: Context, uri: Uri, fileName: String, mimeType: String): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile(fileName, mimeType,requireActivity().cacheDir)
            FileUtils.copyInputStreamToFile(stream,file)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            emitFileForCaller(data,resultCode)
    }
    fun emitFileForCaller(data: Intent?, resultCode:Int){
        if (resultCode == Activity.RESULT_OK && data != null) {
            var array = ArrayList<File>()
            if(data.clipData != null){

                val data: Intent? = data
                if (data?.clipData != null) {

                    val count = data.clipData?.itemCount ?: 0

                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                        val file = getImageFromUri(imageUri)
                        file?.let {
                            array.add(it)
                        }
                    }
                }
            }else if(data?.data != null){

                val imageUri: Uri? = data.data
                val file = getImageFromUri(imageUri)
                file?.let{
                    array.add(it)

                }
            }
            Log.e("attachmentArray",array.toString())

            setParts(array)

        }
    }

    private fun setParts(complaintFiles :ArrayList<File>) {
        if (complaintFiles.isNotEmpty()) {
            parts = ArrayList()
            complaintFiles.map {
                val requestFile: RequestBody =
                    it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                var part = MultipartBody.Part.createFormData(
                    "Attachments",
                    it.name.trim(),
                    requestFile
                )
                parts.add(part)
                Log.e("setParts",parts.toString())

            }
            showMessage("تم اضافة المرفقات بنجاح")
//                complaintFiles = ArrayList()
        }
    }

    private fun showMessage(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                resources.getColor(
                    R.color.color_orange
                )
            )
            .setActionTextColor(resources.getColor(R.color.white)).setAction("Ok")
            {
            }.show()
    }

    fun openGalary(){
        val chooseFile: Intent
        val intent: Intent
        chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(intent, 111)
    }
    private fun observeData() {
        observeDepartments()
        observeCreateComplaint()
        observeLoading()
        observeShowError()
    }

    private fun observeCreateComplaint() {
        viewModel.createComplaintResponse.observe(viewLifecycleOwner){
            it?.let {
               showMessage("تم ارسال الشكوى بنحاح")
                goBack()
            }
        }
    }

    private fun observeDepartments() {
        viewModel.departmentsList.observe(viewLifecycleOwner){
            setDepartmentMenu(it)

        }
    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.progress.visibility = it
            }
        }
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
            showMessage(it)
            }
        }
    }

    private fun setDepartmentMenu(departmentList:ArrayList<String>) {
        binding.departmentTextView.setText(R.string.department)
        val sectorsAdapter = ArrayAdapter(
            requireContext(), R.layout.dropdown_item,
            departmentList
        )
        binding.departmentTextView.setAdapter(sectorsAdapter)
        binding.departmentTextView.setOnItemClickListener{ parent, view, position, rowId ->
                department = parent.getItemAtPosition(position) as String
                isDepartmentSelected = true
            }

    }

    private fun handleBack() {
        binding.backbtn.setOnClickListener {
            goBack()
        }
    }

    private fun goBack() {
        if (from_where == "hr_profile") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToProfileFragment("hr")
            findNavController().navigate(action)
        } else if (from_where == "payment_profile") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToProfileFragment("payment")
            findNavController().navigate(action)
        } else if (from_where == "hr_view_complaints") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToViewComplaintsFragment("hr_profile")
            findNavController().navigate(action)
        } else if (from_where == "payment_view_complaints") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToViewComplaintsFragment("payment_profile")
            findNavController().navigate(action)

        }
    }


}