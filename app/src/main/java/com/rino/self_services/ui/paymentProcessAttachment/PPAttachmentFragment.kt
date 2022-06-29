package com.rino.self_services.ui.paymentProcessAttachment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPPAttachmentBinding
import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.model.pojo.NavToAttachment
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
//import com.rino.self_services.model.pojo.AttachmentPayment
import com.rino.self_services.ui.payment_process_details.PPAttachmentViewAdapter

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PPAttachmentFragment : Fragment() {

private lateinit var binding:FragmentPPAttachmentBinding
private lateinit var data: NavToAttachment
private lateinit var adapter:PPAttachmentViewAdapter
private  lateinit var seeAll:NavSeeAll
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = arguments?.get("attachments") as NavToAttachment
            seeAll = arguments?.get("nav_to_see_all") as NavSeeAll

            adapter = PPAttachmentViewAdapter(data.attachments){
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(data.attachments[it].url)
                startActivity(openURL)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPPAttachmentBinding.inflate(inflater, container, false)
        startShimmer()

        binding.ppAttachmentRv.layoutManager = LinearLayoutManager(requireContext())
        binding.ppAttachmentRv.adapter = adapter
        adapter.notifyDataSetChanged()
        stopShimmer()
        if (data.attachments.isEmpty()){
            showMessage("لم يتم اضافه مرفقات لهذا الطلب")
        }
        binding.backbtn.setOnClickListener {
            if (data.isPaymentProcess){
                var action = PPAttachmentFragmentDirections.actionPPAttachmentFragmentToPaymentProcessDetailsFragment(NavToDetails(data.meOrOther,data.id,data.isActionBefore)
                    , seeAll )
                findNavController().navigate(action)
            }else{
                var action =  PPAttachmentFragmentDirections.actionPPAttachmentFragmentToHRClearanceDetailsFragment(
                    HRClearanceDetailsRequest(data.enity!!,data.id,data.isActionBefore,data.meOrOther)
                    ,seeAll)
                findNavController().navigate(action)
            }
        }
        return binding.root
    }

    private fun stopShimmer() {
        Log.e("shimmer","stop")
        binding.shimmer.stopShimmer()
        binding.ppAttachmentRv.visibility = View.VISIBLE
        binding.shimmer.visibility = View.GONE
    }

    private fun startShimmer() {
        Log.e("shimmer","start")
        binding.shimmer.visibility = View.VISIBLE
        binding.ppAttachmentRv.visibility = View.GONE
        binding.shimmer.startShimmer()
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


}