package com.rino.self_services.ui.managmentAlarts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentManagmentAlartsBinding
import com.rino.self_services.model.pojo.managementAlerts.Data

import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsResponse
import com.rino.self_services.ui.hrClearanceHome.*
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagementAlertsFragment : Fragment() {
    private  val viewModel: ManagementsAlertsViewModel by viewModels()
    private lateinit var binding: FragmentManagmentAlartsBinding
    private lateinit var hrClearanceMainAdapter: ManagementAlertsMainAdapter
    private lateinit var hrClearanceList: ArrayList<Data>
    private lateinit var searchHistoryAdapter: ManagementAlertsSubAdapter
    private lateinit var periodAdapter: PeriodAdapter

    private lateinit var searchHistoryList: ArrayList<Data>
    private lateinit var periodTimeList_ar: ArrayList<String>
    private lateinit var periodTimeList_en: ArrayList<String>
    private lateinit var hrClearanceHomeResponse: ManagementAlertsResponse
    private  var notificationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        periodTimeList_ar = arrayListOf(" منذ عامين "," السنة السابقة "," السنة الحالية "," الشهر السابق "," الشهر الحالى "," الاسبوع السابق "," الاسبوع الحالى "," الكل ")
        periodTimeList_en = arrayListOf("twoyearsago","lastyear","year","lastmonth","month","lastweek","week","all")
        binding.alertsRecycle.visibility = View.GONE
        hrClearanceList = arrayListOf()
        searchHistoryList = arrayListOf()
        periodAdapter = PeriodAdapter(periodTimeList_ar,viewModel)
        hrClearanceMainAdapter = ManagementAlertsMainAdapter(hrClearanceList,viewModel,requireContext())
        hrClearanceMainAdapter.updateItems(hrClearanceList)
        searchHistoryAdapter = ManagementAlertsSubAdapter(searchHistoryList, viewModel,requireContext())
        searchHistoryAdapter.updateItems(searchHistoryList)
        setUpUI()
        handleBackButton()
        observeData()
        hrClearanceMainAdapter.updateItems(emptyList())
    }
    private fun handleBackButton() {
        binding.backbtn.setOnClickListener {
            val action =
                ManagementAlertsFragmentDirections.actionManagementAlertsFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }
    private fun observeData() {
        observeHistoryData()
        observeNoData()
        observeSearchHistoryData()
        observeNavigationCount()
        observeNavToSeeAll()
        observeNavToServiceDetails()
        observeLoading()
        observeShowError()
    }
    private fun observeNavigationCount() {
        viewModel.getNotificationCount()
        viewModel.getNotificationCount.observe(viewLifecycleOwner) {
            it?.let {
                binding.countTxt.text = (it.data?.managementAlerts ?: 0).toString()
                notificationCount = (it.data?.managementAlerts?.let { it1 -> it.data?.managementAlerts?.plus(it1) }) ?: 0
            }
        }
    }
    private fun observeNoData() {
        viewModel.noData.observe(viewLifecycleOwner) {
//            binding.historyRecycle.visibility = View.GONE
            binding.searchRecycle.visibility = View.GONE
            binding.noDataAnim.visibility = View.VISIBLE
            binding.textNoData.visibility = View.VISIBLE
        }
    }


    private fun observeHistoryData() {
   //     viewModel.getManagementAlertsData()
        viewModel.getPaymentData.observe(viewLifecycleOwner) {
            it?.let {
                hrClearanceHomeResponse = it
                hrClearanceMainAdapter.updateItems(it.data)
                hrClearanceList = it.data
            }
            binding.searchRecycle.visibility = View.GONE
            binding.alertsRecycle.visibility = View.VISIBLE
            binding.noDataAnim.visibility = View.GONE
            binding.textNoData.visibility = View.GONE
        }
    }

    private fun observeSearchHistoryData() {
        viewModel.getSearchHistoryData.observe(viewLifecycleOwner) {
            it?.let {
                binding.alertsRecycle.visibility = View.GONE
                binding.searchRecycle.visibility = View.VISIBLE
                searchHistoryAdapter.updateItems(it.data)
                viewModel.viewLoading(View.GONE)
                binding.noDataAnim.visibility = View.GONE
                binding.textNoData.visibility = View.GONE
            }
        }
    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                //     binding.progress.visibility = it
                if(it == View.VISIBLE)
                {
                    binding.shimmer.visibility = View.VISIBLE
                    binding.shimmer.startShimmer()
                    binding.alertsRecycle.visibility = View.GONE
                    binding.noDataAnim.visibility = View.GONE
                    binding.textNoData.visibility = View.GONE
                }
                else if(it == View.GONE){
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE

                }
            }
        }
    }

    private fun observeNavToSeeAll() {
//        viewModel.navToSeeAll.observe(viewLifecycleOwner) {
//            it?.let {
//                navToSeeAll(it)
//            }
//        }
    }

    private fun observeNavToServiceDetails() {
//        viewModel.navToTaskDetails.observe(viewLifecycleOwner) {
//            it?.let {
//                navToServiceDetails(it)
//            }
//        }
    }

//    private fun navToServiceDetails(request: HRClearanceDetailsRequest) {
//        val action = HrClearanceHomeFragmentDirections.actionHrClearanceHomeFragmentToHRClearanceDetailsFragment(request,
//            NavSeeAll("","","")
//        )
//        findNavController().navigate(action)
//    }
//
//
//    private fun navToSeeAll(navSeeAll: NavSeeAll) {
//        val action = HrClearanceHomeFragmentDirections.actionHrClearanceHomeFragmentToSeeAllHrClearanceFragment(navSeeAll)
//        findNavController().navigate(action)
//    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                if(it.contains("Time out")){
                    binding.noDataAnim.visibility = View.VISIBLE
                    binding.textNoData.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_timeout)
                    binding.textNoData.text = getString(R.string.timeout_msg)
                    binding.searchRecycle.visibility = View.GONE
                    binding.alertsRecycle.visibility = View.GONE
                }
                else if(it.contains("server is down"))
                {
                    binding.noDataAnim.visibility = View.VISIBLE
                    binding.textNoData.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_server_error2)
                    binding.textNoData.text = getString(R.string.server_error_msg)
                    binding.searchRecycle.visibility = View.GONE
                    binding.alertsRecycle.visibility = View.GONE                    }
                else{
                    showMessage(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        (activity as MainActivity).bottomNavigation.isGone = true
//        //  checkNetwork(serviceId)
//        registerConnectivityNetworkMonitor()
        //  setPeriodTimeMenuItems()
    }

    private fun setUpUI() {
        binding.notificationBtn.setOnClickListener {
            navToNotification()
        }
        binding.countTxt.setOnClickListener {
            navToNotification()
        }
        binding.profileBtn.setOnClickListener {
            navToProfile()
        }
        binding.mSearch.setQueryHint(getString(R.string.search_hint));
        binding.alertsRecycle.visibility = View.VISIBLE
        binding.alertsRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hrClearanceMainAdapter
        }
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = true
        binding.periodRecycle.apply {
            layoutManager = linearLayoutManager
            adapter = periodAdapter
        }
        binding.searchRecycle.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchHistoryAdapter
        }

        binding.mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.searchMARequest(query)
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {

                return false
            }
        })

    }

    private fun showMessage(it:String) {
        Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_orange)).setActionTextColor(ContextCompat.getColor(
                requireContext(),
                R.color.white)).setAction(getString(R.string.dismiss))
            {
            }.show()
    }

    private fun navToProfile() {
        val action = ManagementAlertsFragmentDirections.managementAlertsFragmentToProfileFragment("MA")
        findNavController().navigate(action)
    }

    private fun navToNotification() {
        if(notificationCount == 0){
            showMessage("لا توجد اشعارات حتي الان")
        }
        else {
            val action =
                ManagementAlertsFragmentDirections.managementAlertsFragmentToNotificationsFragment(
                    "MA"
                )
            findNavController().navigate(action)
        }
    }



}