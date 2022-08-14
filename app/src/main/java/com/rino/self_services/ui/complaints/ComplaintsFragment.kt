package com.rino.self_services.ui.complaints

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentComplaintsBinding
import com.rino.self_services.ui.pdfViewer.MyFile
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
    private lateinit var serviceAdapter: PreviewAttachmentAdapter
    private lateinit var servicesList: ArrayList<File>
    var filesArray = ArrayList<File>()
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
        serviceAdapter = PreviewAttachmentAdapter(arrayListOf(), requireActivity(),viewModel)

        setUpUI()
      //  serviceAdapter.updateItems(servicesList)
        observeData()
        setBodyLength()
        addAttachments()
        addComplaint()
        handleBack()

    }

    private fun setUpUI() {
        binding.attachmentRecycle.visibility = View.GONE
        binding.attachmentRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }
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
            setParts(filesArray)
            val body =   binding.notesEditTxt.text.toString()
            val officer = binding.administratorEditTxt.text.toString()
            if(validateData(officer,department,body)) {
                viewModel.createComplaint(department, officer, body, parts)
            }
            else{
                showMessage("برجاء اضافه البيانات المطلوبة")
            }
        }
    }

    private fun validateData(officer:String,department:String,complaintBody:String) :Boolean{
        if (officer.isEmpty()) binding.textInputAdministrator.error = getString(R.string.required_field) else binding.textInputAdministrator.error = null
        if (!isDepartmentSelected)   binding.departmentTextInputLayout.error = getString(R.string.required_field) else binding.departmentTextInputLayout.error = null
        if (complaintBody.isEmpty()) binding.textInputNotes.error = getString(R.string.required_field) else binding.textInputNotes.error = null

        return (officer.isNotEmpty() && department.isNotEmpty() && complaintBody.isNotEmpty() && complaintBody.length < 500)
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

            if(data.clipData != null){

                val data: Intent? = data
                if (data?.clipData != null) {

                    val count = data.clipData?.itemCount ?: 0

                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                        val file = getImageFromUri(imageUri)
                        file?.let {
                            filesArray.add(it)
                        }
                    }
                }
            }else if(data?.data != null){

                val imageUri: Uri? = data.data
                val file = getImageFromUri(imageUri)
                file?.let{
                    filesArray.add(it)

                }
            }
            Log.e("attachmentArray",filesArray.toString())
            binding.attachmentRecycle.visibility= View.VISIBLE
            serviceAdapter.updateItems(filesArray)


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
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_orange)).setActionTextColor(
                ContextCompat.getColor(
                requireContext(),
                R.color.white)).setAction(getString(R.string.dismiss))
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
        observeAttachmentDeleted()
        observeNavToPDF()
        observeShowError()
    }

    private fun observeNavToPDF() {
        viewModel.navToPdfPreview.observe(viewLifecycleOwner){
            it?.let {
              //  navToPdfPreview(it)
                showViewerPopup(it)
            }
        }
    }

    private fun showViewerPopup(file: File) {
        if(file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")||file.toString().contains(".pdf")) {
            val inflater =
                requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.preview_attachment_popup, null)
            binding.mainLayout.alpha = 0.3f
            var afterPopup = PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            afterPopup.setOnDismissListener { binding.mainLayout.alpha = 1f }
            afterPopup.isOutsideTouchable = true
            afterPopup.isFocusable = true
            //        afterPopup.animationStyle = R.anim.down_to_up
            afterPopup.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
            afterPopup.showAtLocation(requireView(), Gravity.BOTTOM, 0, 0)
            val attachment_pdf = view.findViewById<PDFView>(R.id.pdfView)
            val attachment_img = view.findViewById<ImageView>(R.id.imageView)
            val back_img = view.findViewById<ImageView>(R.id.backbtn)
            back_img.setOnClickListener {
                afterPopup.dismiss()
            }
            if (file.exists()) {
                if (file.toString().contains(".jpg") || file.toString()
                        .contains(".jpeg") || file.toString().contains(".png")
                ) {
                    val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    attachment_img.visibility = View.VISIBLE
                    attachment_pdf.visibility = View.GONE
                    attachment_img.setImageBitmap(myBitmap)
                } else if (file.toString().contains(".pdf")) {
                    attachment_img.visibility = View.GONE
                    attachment_pdf.visibility = View.VISIBLE
                    attachment_pdf.fromFile(file)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .onPageError { page, _ ->
                            Toast.makeText(
                                requireContext(),
                                "Error at page: $page", Toast.LENGTH_LONG
                            ).show()
                        }
                        .load()
                }


            }
        } else
        {
            showMessage("لا يمكن فتح هذا النوع من الملفات حاليا")
        }
    }

    private fun navToPdfPreview(file:File) {
        if (from_where == "hr_profile") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToPdfViewerFragment("hr",  MyFile(file))
            findNavController().navigate(action)
        } else if (from_where == "payment_profile") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToPdfViewerFragment("payment", MyFile(file) )
            findNavController().navigate(action)
        } else if (from_where == "hr_view_complaints") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToPdfViewerFragment("hr_profile",  MyFile(file))
            findNavController().navigate(action)
        } else if (from_where == "payment_view_complaints") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToPdfViewerFragment("payment_profile",  MyFile(file))
            findNavController().navigate(action)

        }
    }

    private fun observeAttachmentDeleted() {
        viewModel.attachmentsDeleteItem.observe(viewLifecycleOwner){
            it?.let {
              filesArray.remove(it)
            }
        }
    }

    private fun observeCreateComplaint() {
        viewModel.createComplaintResponse.observe(viewLifecycleOwner){
            it?.let {
               showMessage("تم ارسال الشكوى بنجاح")
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
        }  else if (from_where == "MA_profile") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToProfileFragment("MA")
            findNavController().navigate(action)
        }else if (from_where == "hr_view_complaints") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToViewComplaintsFragment("hr_profile")
            findNavController().navigate(action)
        } else if (from_where == "payment_view_complaints") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToViewComplaintsFragment("payment_profile")
            findNavController().navigate(action)

        }else if (from_where == "MA_view_complaints") {
            val action =
                ComplaintsFragmentDirections.actionComplaintsFragmentToViewComplaintsFragment("MA_profile")
            findNavController().navigate(action)

        }
    }


}