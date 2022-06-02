package com.rino.self_services.ui.viewAttatchment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentViewAttatchmentsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewAttatchmentsFragment : Fragment() {
    private lateinit var binding: FragmentViewAttatchmentsBinding
    private lateinit var serviceAdapter: AttatchmentAdapter
    private lateinit var servicesList: ArrayList<Attachment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewAttatchmentsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        serviceAdapter = AttatchmentAdapter(arrayListOf(), requireActivity())
        servicesList = arrayListOf()
        servicesList.add(Attachment("image1.jpg","https://rino-app-staging.azurewebsites.net/attachments/b5f9401f-e01a-4dd9-85e2-88f009b24a05_IMG_20220105_153702.jpg"))
        servicesList.add(Attachment("amanatjeddahLocationData.xlsx","https://rino-app-staging.azurewebsites.net/attachments/c1122704-2e7a-4267-a4a6-46b999de102c_amanatjeddahLocationData.xlsx"))
        servicesList.add(Attachment("image2.jpg","https://rino-app-staging.azurewebsites.net/attachments/b5f9401f-e01a-4dd9-85e2-88f009b24a05_IMG_20220105_153702.jpg"))
        servicesList.add(Attachment("image3.jpg","https://rino-app-staging.azurewebsites.net/attachments/e285ed06-d33d-4b2d-86a0-9610ea4bcf69_IMG_20220105_153610.jpg"))
        servicesList.add(Attachment("file2.xlsx","https://rino-app-staging.azurewebsites.net/attachments/c1122704-2e7a-4267-a4a6-46b999de102c_amanatjeddahLocationData.xlsx"))
        servicesList.add(Attachment("image4.jpg","https://rino-app-staging.azurewebsites.net/attachments/b5f9401f-e01a-4dd9-85e2-88f009b24a05_IMG_20220105_153702.jpg"))
        setUpUI()
        serviceAdapter.updateItems(servicesList)

    }

    private fun setUpUI() {
        binding.attachmentRecycle.visibility = View.VISIBLE
        binding.attachmentRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }

    }

}