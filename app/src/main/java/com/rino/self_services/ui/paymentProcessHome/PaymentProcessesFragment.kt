package com.rino.self_services.ui.paymentProcessHome

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPaymentProcessesBinding
import com.rino.self_services.model.pojo.payment.Data
import com.rino.self_services.model.pojo.payment.Items
import com.rino.self_services.model.pojo.payment.PaymentHomeResponse
import com.rino.self_services.ui.hrClearanceHome.HrClearanceHomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentProcessesFragment : Fragment() {
    private  val viewModel: PaymentHomeViewModel by viewModels()
    private lateinit var binding: FragmentPaymentProcessesBinding
    private lateinit var paymentMainItemAdapter: PaymentMainItemAdapter
    private lateinit var paymentList: ArrayList<Data>
    private lateinit var searchHistoryAdapter: SubPaymentItemAdapter
    private lateinit var periodAdapter: PeriodAdapter

    private lateinit var searchHistoryList: ArrayList<Items>
    private lateinit var periodTimeList_ar: ArrayList<String>
    private lateinit var periodTimeList_en: ArrayList<String>
    private lateinit var paymentHomeResponse: PaymentHomeResponse
    private var me_or_others = "me"
    private var notificationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentHomeViewModel.lastSelectedPos = PaymentHomeViewModel.periodTimeList_en.size -1
        arguments?.let {
        //    serviceId = arguments?.get("serviceID").toString().toInt()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentProcessesBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        periodTimeList_ar = arrayListOf(" منذ عامين "," السنة السابقة "," السنة الحالية "," الشهر السابق "," الشهر الحالى "," الاسبوع السابق "," الاسبوع الحالى "," الكل ")
        periodTimeList_en = arrayListOf("twoyearsago","lastyear","year","lastmonth","month","lastweek","week","all")
        binding.historyRecycle.visibility = View.GONE
        viewModel.me_or_others = me_or_others
        paymentList = arrayListOf()
        searchHistoryList = arrayListOf()
        periodAdapter = PeriodAdapter(periodTimeList_ar,viewModel)
        paymentMainItemAdapter = PaymentMainItemAdapter(paymentList,viewModel,requireContext())
        paymentMainItemAdapter.updateItems(paymentList)
        searchHistoryAdapter = SubPaymentItemAdapter(searchHistoryList, viewModel,requireContext())
        searchHistoryAdapter.updateItems(searchHistoryList)
        setUpUI()
//        checkNetwork(serviceId)
        observeData()
        handleBackButton()
        paymentMainItemAdapter.updateItems(emptyList())
    }

    private fun handleBackButton() {
        binding.backbtn.setOnClickListener {
            val action =
                PaymentProcessesFragmentDirections.paymentToHome()
            findNavController().navigate(action)
        }
    }

    private fun observeData() {
        observeHistoryData()
        observeNoData()
        observeSearchHistoryData()
        observeNavToSeeAll()
        observeNavigationCount()
        observeNavToServiceDetails()
        observeLoading()
        observeShowError()
    }

    private fun observeNavigationCount() {
        viewModel.getNotificationCount()
        viewModel.getNotificationCount.observe(viewLifecycleOwner) {
            it?.let {
           binding.countTxt.text = it.data.toString()
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
        viewModel.getPaymentData()
        viewModel.getPaymentData.observe(viewLifecycleOwner) {
            it?.let {
                paymentHomeResponse = it
                paymentMainItemAdapter.updateItems(it.data)
                paymentList = it.data
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
             //   binding.progress.visibility = it
                if(it == View.VISIBLE)
                {
                    Log.e("shimmer","start")
                    binding.shimmer.visibility = View.VISIBLE
                    binding.shimmer.startShimmer()
                    binding.historyRecycle.visibility = View.GONE
                    binding.searchHistoryRecycle.visibility = View.GONE
                    binding.noDataAnim.visibility = View.GONE
                    binding.textNoData.visibility = View.GONE
                }
                else{
                    Log.e("shimmer","stop")
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

    private fun navToServiceDetails(navToDetails: NavToDetails) {
        val action = PaymentProcessesFragmentDirections.actionPaymentProcessesFragmentToPaymentProcessDetailsFragment(
            navToDetails,NavSeeAll("","",""))
        findNavController().navigate(action)
    }


    private fun navToSeeAll(navSeeAll: NavSeeAll) {
        val action = PaymentProcessesFragmentDirections.paymentProcessToSeeAll(navSeeAll)
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


    private fun showMessage(it: String) {
        Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_orange)).setActionTextColor(
                ContextCompat.getColor(
                requireContext(),
                R.color.white)).setAction(getString(R.string.dismiss))
            {
            }.show()
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
        binding.countTxt.setOnClickListener {
                navToNotification()
        }
        binding.notificationBtn.setOnClickListener {
                navToNotification()
        }
        binding.profileBtn.setOnClickListener {
            navToProfile()
        }
        binding.mSearch.setQueryHint(getString(R.string.search_hint));
        binding.historyRecycle.visibility = View.VISIBLE
        binding.historyRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentMainItemAdapter
        }
        val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
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
                    viewModel.searchHistoryDataByService(query)
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {

                return false
            }
        })

    }

    private fun navToProfile() {
        val action = PaymentProcessesFragmentDirections.actionPaymentProcessesFragmentToProfileFragment("payment")
        findNavController().navigate(action)
    }

    private fun navToNotification() {
        if(notificationCount == 0){
            showMessage("لا توجد اشعارات حتي الان")
        }
        else {
            val action =
                PaymentProcessesFragmentDirections.actionPaymentProcessesFragmentToNotificationsFragment(
                    "payment"
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
                    viewModel.getPaymentData()
                    true
                }
                R.id.others_item -> {
                    viewModel.me_or_others = "others"
                    viewModel.getPaymentData()
                    true
                }
                else-> false

            }
        }
    }
}