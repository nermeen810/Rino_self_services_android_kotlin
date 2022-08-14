package com.rino.self_services.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentNotificationsBinding
import com.rino.self_services.model.pojo.notifications.Data
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var serviceAdapter: NotificationAdapter
    private lateinit var servicesList: ArrayList<Data>
    private var fromWhere = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fromWhere = arguments?.get("fromWhere").toString()
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
        servicesList = arrayListOf()
        serviceAdapter = NotificationAdapter(requireContext(), arrayListOf(), viewModel) {
            getPressedItemData(it)
        }
        when(fromWhere){
            "payment"-> {
             serviceAdapter.process = 0
            }
            "hr"-> {
                serviceAdapter.process = 1
            }
            "MA"-> {
                serviceAdapter.process = 2
            }
        }
        setUpUI()
        observeAllNotification()
        observeSetNotificationAsRead()
        observeLoading()
        observeShowError()
        serviceAdapter.updateItems(servicesList)

    }

    fun getPressedItemData(notification: Data) {


        if (notification.subcategory.equals("Payment Process")) {
            val action =
                notification.requestid?.let { NavToDetails("me", it, true) }?.let {
                    NotificationsFragmentDirections.actionNotificationsFragmentToPaymentProcessDetailsFragment(
                        it,
                        NavSeeAll("", "", "")
                    )
                }
            if (action != null) {
                findNavController().navigate(action)
            }
        } else {

        }

    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner, this::showMsg)
    }

    private fun showMsg(it: String) {
        Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_orange
                )
            ).setActionTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            ).setAction(getString(R.string.dismiss))
            {
            }.show()
    }

    private fun observeSetNotificationAsRead() {
        viewModel.readNotificationPosition.observe(viewLifecycleOwner) {
            it?.let {
                serviceAdapter.setNotificationAsRead(it)
            }
        }
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
            }else if (fromWhere == "MA") {
                val action =
                    NotificationsFragmentDirections.actionNotificationsFragmentToManagementAlertsFragment()
                findNavController().navigate(action)
            }
        }
    }

}