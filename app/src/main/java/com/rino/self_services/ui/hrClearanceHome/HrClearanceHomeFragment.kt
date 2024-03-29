package com.rino.self_services.ui.hrClearanceHome

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
import com.rino.self_services.databinding.FragmentHrClearanceHomeBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.model.pojo.hrClearance.Data
import com.rino.self_services.model.pojo.hrClearance.HrClearanceResponse
import com.rino.self_services.model.pojo.hrClearance.Items
import com.rino.self_services.ui.paymentProcessHome.*

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HrClearanceHomeFragment : Fragment() {
    private  val viewModel: HrClearanceViewModel by viewModels()
    private lateinit var binding: FragmentHrClearanceHomeBinding
    private lateinit var hrClearanceMainAdapter: HrClearanceMainAdapter
    private lateinit var hrClearanceList: ArrayList<Data>
    private lateinit var searchHistoryAdapter: HrClearanceSubAdapter
    private lateinit var periodAdapter: HrPeriodAdapter

    private lateinit var searchHistoryList: ArrayList<Items>
    private lateinit var periodTimeList_ar: ArrayList<String>
    private lateinit var periodTimeList_en: ArrayList<String>
    private lateinit var hrClearanceHomeResponse: HrClearanceResponse
    private var me_or_others = "me"
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
        binding = FragmentHrClearanceHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }
    private fun init() {
        periodTimeList_ar = arrayListOf(" منذ عامين "," السنة السابقة "," السنة الحالية "," الشهر السابق "," الشهر الحالى "," الاسبوع السابق "," الاسبوع الحالى "," الكل ")
        periodTimeList_en = arrayListOf("twoyearsago","lastyear","year","lastmonth","month","lastweek","week","all")
        binding.historyRecycle.visibility = View.GONE
        viewModel.me_or_others = me_or_others
        hrClearanceList = arrayListOf()
        searchHistoryList = arrayListOf()
        periodAdapter = HrPeriodAdapter(periodTimeList_ar,viewModel)
        hrClearanceMainAdapter = HrClearanceMainAdapter(hrClearanceList,viewModel,requireContext())
        hrClearanceMainAdapter.updateItems(hrClearanceList)
        searchHistoryAdapter = HrClearanceSubAdapter(searchHistoryList, viewModel,requireContext())
        searchHistoryAdapter.updateItems(searchHistoryList)
        setUpUI()
        handleBackButton()
//        checkNetwork(serviceId)
        observeData()
        hrClearanceMainAdapter.updateItems(emptyList())
    }
    private fun handleBackButton() {
        binding.backbtn.setOnClickListener {
            val action =
                HrClearanceHomeFragmentDirections.hrToHome()
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
                binding.countTxt.text = it.data.toString()
                notificationCount = it .data
            }
        }
    }
    private fun observeNoData() {
        viewModel.noData.observe(viewLifecycleOwner) {
            binding.historyRecycle.visibility = View.GONE
            binding.searchHistoryRecycle.visibility = View.GONE
            binding.noDataAnim.visibility = View.VISIBLE
            binding.textNoData.visibility = View.VISIBLE
        }
    }


    private fun observeHistoryData() {
        viewModel.getHrData("me")
        viewModel.getPaymentData.observe(viewLifecycleOwner) {
            it?.let {
                hrClearanceHomeResponse = it
                hrClearanceMainAdapter.updateItems(it.data)
                hrClearanceList = it.data
            }
            binding.searchHistoryRecycle.visibility = View.GONE
            binding.historyRecycle.visibility = View.VISIBLE
            binding.noDataAnim.visibility = View.GONE
            binding.textNoData.visibility = View.GONE
        }
    }

    private fun observeSearchHistoryData() {
        viewModel.getSearchHistoryData.observe(viewLifecycleOwner) {
            it?.let {
                binding.historyRecycle.visibility = View.GONE
                binding.searchHistoryRecycle.visibility = View.VISIBLE
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
                    binding.historyRecycle.visibility = View.GONE
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
        viewModel.navToSeeAll.observe(viewLifecycleOwner) {
            it?.let {
                navToSeeAll(it)
            }
        }
    }

    private fun observeNavToServiceDetails() {
        viewModel.navToTaskDetails.observe(viewLifecycleOwner) {
            it?.let {
                navToServiceDetails(it)
            }
        }
    }

    private fun navToServiceDetails(request: HRClearanceDetailsRequest) {
        val action = HrClearanceHomeFragmentDirections.actionHrClearanceHomeFragmentToHRClearanceDetailsFragment(request,NavSeeAll("","",""))
        findNavController().navigate(action)
    }


    private fun navToSeeAll(navSeeAll: NavSeeAll) {
        val action = HrClearanceHomeFragmentDirections.actionHrClearanceHomeFragmentToSeeAllHrClearanceFragment(navSeeAll)
        findNavController().navigate(action)
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                if(it.contains("Time out")){
                    binding.noDataAnim.visibility = View.VISIBLE
                    binding.textNoData.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_timeout)
                    binding.textNoData.text = getString(R.string.timeout_msg)
                    binding.searchHistoryRecycle.visibility = View.GONE
                    binding.historyRecycle.visibility = View.GONE
                }
                else if(it.contains("server is down"))
                {
                    binding.noDataAnim.visibility = View.VISIBLE
                    binding.textNoData.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_server_error2)
                    binding.textNoData.text = getString(R.string.server_error_msg)
                    binding.searchHistoryRecycle.visibility = View.GONE
                    binding.historyRecycle.visibility = View.GONE                    }
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
        bottomNavigationSetup()
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
        binding.historyRecycle.visibility = View.VISIBLE
        binding.historyRecycle.apply {
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
        binding.searchHistoryRecycle.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchHistoryAdapter
        }

        binding.mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.searchHrRequest(query)
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
        val action = HrClearanceHomeFragmentDirections.actionHrClearanceHomeFragmentToProfileFragment("hr")
        findNavController().navigate(action)
    }

    private fun navToNotification() {
        if(notificationCount == 0){
            showMessage("لا توجد اشعارات حتي الان")
        }
        else {
            val action =
                HrClearanceHomeFragmentDirections.actionHrClearanceHomeFragmentToNotificationsFragment(
                    "hr"
                )
            findNavController().navigate(action)
        }
    }

    private fun bottomNavigationSetup() {
        //      binding.bottomNavigation.
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.me_item -> {
                    viewModel.me_or_others = "me"
                    viewModel.getHrData("me")
                    true
                }
                R.id.others_item -> {
                    viewModel.me_or_others = "others"
                    viewModel.getHrData("others")
                    true
                }
                else-> false

            }
        }
    }

}