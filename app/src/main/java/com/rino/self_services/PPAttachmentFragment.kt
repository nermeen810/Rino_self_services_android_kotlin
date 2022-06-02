package com.rino.self_services

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.rino.self_services.databinding.FragmentPPAttachmentBinding
import com.rino.self_services.model.pojo.AttachmentPayment
import com.rino.self_services.ui.payment_process_details.PPAttachmentViewAdapter

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PPAttachmentFragment : Fragment() {

private lateinit var binding:FragmentPPAttachmentBinding
private lateinit var array: Array<AttachmentPayment>
private lateinit var adapter:PPAttachmentViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            array = arguments?.get("attachments") as Array<AttachmentPayment>


            adapter = PPAttachmentViewAdapter(array){
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(array[it].url)
                startActivity(openURL)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPPAttachmentBinding.inflate(inflater, container, false)
        binding.ppAttachmentRv.layoutManager = LinearLayoutManager(requireContext())
        binding.ppAttachmentRv.adapter = adapter
        adapter.notifyDataSetChanged()
        return binding.root
    }


}