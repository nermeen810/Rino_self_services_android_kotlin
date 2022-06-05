package com.rino.self_services.ui.viewAttatchment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rino.self_services.databinding.FragmentViewAttatchmentsBinding
import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewAttatchmentsFragment : Fragment() {
    private lateinit var binding: FragmentViewAttatchmentsBinding
    private lateinit var serviceAdapter: AttachmentAdapter
    private lateinit var servicesList: ArrayList<Attachment>
    private lateinit var hrClearanceDetailsRequest: HRClearanceDetailsRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            servicesList = arrayListOf()
             val attachment =  arguments?.get("attachment") as Array<Attachment>
            hrClearanceDetailsRequest =  arguments?.get("back_to_hr_details") as HRClearanceDetailsRequest

            for(item in attachment){
                servicesList.add(item)
            }

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
        serviceAdapter = AttachmentAdapter(arrayListOf(), requireActivity())
        setUpUI()
        serviceAdapter.updateItems(servicesList)

    }

    private fun setUpUI() {
        handleBackButton()
        binding.attachmentRecycle.visibility = View.VISIBLE
        binding.attachmentRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }

    }
    private fun handleBackButton() {
//        binding.backbtn.setOnClickListener {
//            val action =
//                ViewAttatchmentsFragmentDirections.hrViewAttachmentsToHrClearanceDetails(hrClearanceDetailsRequest)
//            findNavController().navigate(action)
//        }
    }
}