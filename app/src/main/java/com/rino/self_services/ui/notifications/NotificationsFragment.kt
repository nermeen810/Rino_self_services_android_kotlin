package com.rino.self_services.ui.notifications

import android.os.Bundle
import android.system.Os.close
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentNotificationsBinding
import com.rino.self_services.databinding.FragmentViewAttatchmentsBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.model.pojo.notifications.Data
import com.rino.self_services.ui.viewAttatchment.Attachment
import com.rino.self_services.ui.viewAttatchment.AttachmentAdapter
import com.rino.self_services.ui.viewAttatchment.ViewAttatchmentsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var serviceAdapter: NotificationAdapter
    private lateinit var servicesList: ArrayList<Data>
    private var fromWhere =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fromWhere =  arguments?.get("fromWhere").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }
    private fun init() {
        viewModel.getAllNotification()
        serviceAdapter = NotificationAdapter(arrayListOf(), requireActivity())
        setUpUI()
        observeAllNotification()
        observeSetNotificationAsRead()
        observeLoading()
        serviceAdapter.updateItems(servicesList)

    }

    private fun observeSetNotificationAsRead() {

    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.progress.visibility = it
            }
        }
    }


    private fun observeAllNotification() {
        viewModel.getAllNotification()
        viewModel.getAllNotification.observe(viewLifecycleOwner) {
            it?.let {
                serviceAdapter.updateItems(it.data)
                servicesList = it.data
                binding.notificationRecycle.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpUI() {
        handleBackButton()
        binding.notificationRecycle.visibility = View.VISIBLE
        binding.notificationRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }

    }
    private fun handleBackButton() {
        binding.backbtn.setOnClickListener {
            if (fromWhere == "hr") {
                val action =
                    NotificationsFragmentDirections.actionNotificationsFragmentToHrClearanceHomeFragment()
                findNavController().navigate(action)
            } else if (fromWhere == "payment") {
                val action =
                    NotificationsFragmentDirections.actionNotificationsFragmentToPaymentProcessesFragment()
                findNavController().navigate(action)
            }
        }
    }

}