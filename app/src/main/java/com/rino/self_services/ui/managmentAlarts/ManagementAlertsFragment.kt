package com.rino.self_services.ui.managmentAlarts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentManagmentAlartsBinding
import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsResponse
import com.rino.self_services.ui.paymentProcessHome.PaymentHomeViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagementAlertsFragment : Fragment() {
    val viewModel: ManagementsAlertsViewModel by viewModels()
    private lateinit var binding: FragmentManagmentAlartsBinding
    private lateinit var managementAlertsAdapter: ManagementAlertsAdapter
    private lateinit var periodAdapter: PeriodAdapter
    private lateinit var managementAlertsList: ArrayList<ManagementAlertsResponse>
    private lateinit var periodTimeList_ar: ArrayList<String>
    private lateinit var periodTimeList_en: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ManagementsAlertsViewModel.lastSelectedPos = PaymentHomeViewModel.periodTimeList_en.size - 1

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManagmentAlartsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        periodTimeList_ar = arrayListOf(
            " منذ عامين ",
            " السنة السابقة ",
            " السنة الحالية ",
            " الشهر السابق ",
            " الشهر الحالى ",
            " الاسبوع السابق ",
            " الاسبوع الحالى ",
            " الكل "
        )
        periodTimeList_en = arrayListOf(
            "twoyearsago",
            "lastyear",
            "year",
            "lastmonth",
            "month",
            "lastweek",
            "week",
            "all"
        )
        setupUI()
        observeData()
        handleBack()
    }

    private fun setupUI() {
        binding.alertsRecycle.visibility = View.GONE
        //  paymentList = arrayListOf()
        managementAlertsAdapter = ManagementAlertsAdapter(arrayListOf(), viewModel)
        binding.alertsRecycle.visibility = View.VISIBLE
        binding.alertsRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = managementAlertsAdapter
        }
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = true
        binding.periodRecycle.apply {
            layoutManager = linearLayoutManager
            adapter = periodAdapter
        }
        binding.mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    //               viewModel.searchHistoryDataByService(query)
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {

                return false
            }
        })
    }

    private fun observeData() {
        observeLoading()
        observeShowError()
    }

    private fun handleBack() {
        binding.backbtn.setOnClickListener {
//            val action =
//                PaymentProcessesFragmentDirections.paymentToHome()
//            findNavController().navigate(action)
        }
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                //        binding.progress.visibility = it
                if (it == View.VISIBLE) {
                    binding.shimmer.visibility = View.VISIBLE
                    binding.shimmer.startShimmer()
                    binding.alertsRecycle.visibility = View.GONE
                    binding.noDataAnim.visibility = View.GONE
                    binding.textNoData.visibility = View.GONE
                } else if (it == View.GONE) {
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.alertsRecycle.visibility = View.VISIBLE
                    binding.noDataAnim.visibility = View.GONE
                    binding.textNoData.visibility = View.GONE
                }
            }
        }
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                if (it.contains("Time out")) {
                    binding.noDataAnim.visibility = View.VISIBLE
                    binding.textNoData.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_timeout)
                    binding.textNoData.text = getString(R.string.timeout_msg)
                    binding.alertsRecycle.visibility = View.GONE
                } else if (it.contains("server is down")) {
                    binding.noDataAnim.visibility = View.VISIBLE
                    binding.textNoData.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_server_error2)
                    binding.textNoData.text = getString(R.string.server_error_msg)
                    binding.alertsRecycle.visibility = View.GONE
                } else {
                    showMsg(it)
                }
            }
        }
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


}