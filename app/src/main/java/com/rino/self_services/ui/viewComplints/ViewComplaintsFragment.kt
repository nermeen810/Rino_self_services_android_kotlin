package com.rino.self_services.ui.viewComplints

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentViewComplaintsBinding
import com.rino.self_services.model.pojo.NavToAttachment
import com.rino.self_services.model.pojo.complaints.ComplaintItemResponse
import com.rino.self_services.model.pojo.payment.Data
import com.rino.self_services.model.pojo.payment.PaymentHomeResponse
import com.rino.self_services.ui.hrClearanceDetails.HRClearanceDetailsFragmentDirections
import com.rino.self_services.ui.paymentProcessHome.PaymentMainItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewComplaintsFragment : Fragment() {
    val viewModel: ViewComplaintsViewModel by viewModels()
    private lateinit var binding: FragmentViewComplaintsBinding
    private lateinit var paymentMainItemAdapter: ComplaintAdapter
    private lateinit var paymentList: ArrayList<ComplaintItemResponse>


    private var from_where = ""
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
        // Inflate the layout for this fragment
        binding = FragmentViewComplaintsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setupUI()
        observeData()
        handleBack()
        addComplaint()
    }

    private fun observeData() {
        observeComplaintsList()
        observeNavToAttachments()
        observeLoading()
        observeShowError()
    }

    private fun observeNavToAttachments() {
        viewModel.navToViewAttachments.observe(viewLifecycleOwner){
            val action =
                ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToViewAttatchmentsFragment(from_where,it.toTypedArray())
            findNavController().navigate(action)

        }
    }

    private fun observeComplaintsList() {
        viewModel.getComplaintsList()
        viewModel.complaintResponse.observe(viewLifecycleOwner) {
            it?.let {
                paymentMainItemAdapter.updateItems(it)
                paymentList = it
            }
            binding.complaintsRecycle.visibility = View.VISIBLE
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
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                        resources.getColor(
                            R.color.color_orange
                        )
                    )
                    .setActionTextColor(resources.getColor(R.color.white)).setAction(getString(R.string.dismiss))
                    {
                    }.show()
            }
        }
    }
    private fun setupUI() {
        binding.complaintsRecycle.visibility = View.GONE
        paymentList = arrayListOf()
        paymentMainItemAdapter = ComplaintAdapter(paymentList,viewModel)
        paymentMainItemAdapter.updateItems(paymentList)
        binding.complaintsRecycle.visibility = View.VISIBLE
        binding.complaintsRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentMainItemAdapter
        }
    }

    private  fun addComplaint(){
        binding.fab.setOnClickListener{
            if (from_where == "hr_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToComplaintsFragment("hr_view_complaints")
                findNavController().navigate(action)
            } else if (from_where == "payment_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToComplaintsFragment("payment_view_complaints")
                findNavController().navigate(action)
            }
        }
    }

    private fun handleBack() {
        binding.backbtn.setOnClickListener {

            if (from_where == "hr_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToProfileFragment("hr")
                findNavController().navigate(action)
            } else if (from_where == "payment_profile") {
                val action =
                    ViewComplaintsFragmentDirections.actionViewComplaintsFragmentToProfileFragment("payment")
                findNavController().navigate(action)
            }
        }
    }
}