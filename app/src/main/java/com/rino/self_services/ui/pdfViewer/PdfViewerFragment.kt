package com.rino.self_services.ui.pdfViewer

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPdfViewerBinding
import java.io.File


class PdfViewerFragment : Fragment() {
    lateinit var binding: FragmentPdfViewerBinding
    lateinit var file: File
    private var from_where = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val myFile =  arguments?.get("file") as MyFile
            from_where =  arguments?.get("from_where").toString()
            file = myFile.file
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPdfViewerBinding.inflate(inflater, container, false)
        init()
        handleBackButton()
        return binding.root
    }

    private fun init() {
        if (file.exists()) {
            val mime = getMimeType(file)
            if(file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png"))
            {
                val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.imageView.visibility =View.VISIBLE
                binding.pdfView.visibility = View.GONE
                binding.imageView.setImageBitmap(myBitmap)
            }
            else if(file.toString().contains(".pdf"))
            {
                binding.imageView.visibility =View.GONE
                binding.pdfView.visibility = View.VISIBLE
                showPdfFromFile()
            }

        }
    }
    fun getMimeType(file: File): String? {
        val uri = Uri.fromFile(file)
        val cR = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
    private fun showPdfFromFile() {
        binding.pdfView.fromFile(file)
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
    private fun handleBackButton() {
        binding.backbtn.setOnClickListener {
            findNavController().navigateUp()

//            val action =
//                PdfViewerFragmentDirections.actionPdfViewerFragmentToComplaintsFragment(from_where)
//            findNavController().navigate(action)

//            findNavController().popBackStack(R.id.pdfViewerFragment,false)

        }
    }
}