package com.rino.self_services.ui.seeAllPayment

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentSeeAllPaymentProcessBinding

import com.rino.self_services.model.pojo.SeeAllRequest
import com.rino.self_services.ui.hrClearanceDetails.HRClearanceDetailsFragmentDirections
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SeeAllPaymentProcessFragment : Fragment() {
    private lateinit var adapter:SeeAllPaymentProcessRVAdapter
    val viewModel: SeeAllPaymentProcessViewModel by viewModels()
    private lateinit var binding: FragmentSeeAllPaymentProcessBinding
    private lateinit var period:SeeAllRequest
    private var totalPages = 1
    private lateinit var navSeeAll: NavSeeAll

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navSeeAll = arguments?.get("nav_see_all") as NavSeeAll
            period = SeeAllRequest("token","requests",navSeeAll.me_or_others,navSeeAll.startPeriod,navSeeAll.endPeriod,1)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSeeAllPaymentProcessBinding.inflate(inflater, container, false)

        observeLoading()
        overseError()
        handleBackBotton()
        adapter = SeeAllPaymentProcessRVAdapter(period.currentFutuer,ArrayList()){
            getPressesdItemIndex(it)
        }

        binding.paymentProcessSeeAllRv.adapter = adapter
        binding.paymentProcessSeeAllRv.layoutManager = LinearLayoutManager(this.context)

        viewModel.getData(period)
        oserveData()
        binding.paymentProcessSeeAllRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    if (viewModel.pageNumber < totalPages && viewModel.loading.value == View.GONE){
                        viewModel.pageNumber += 1
                        viewModel.getData(period)
                    }
                }
            }
        })
        return binding.root
    }
    private fun handleBackBotton() {
        binding.backbtn.setOnClickListener {
            val action =
                SeeAllPaymentProcessFragmentDirections.seeAllPaymentProcessFragmentToPaymentHome()
            findNavController().navigate(action)
        }
    }
    fun oserveData(){
        viewModel.seeAllPaymentProcessData.observe(viewLifecycleOwner){
            it?.let {
                totalPages = it.totalPages
                it.data.let { it1 -> adapter.updateItems(viewModel.seeAllarray)
                    }
        }
    }
    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.progressBar.visibility = it
            }
        }
    }
    private fun overseError(){
        viewModel.setError.observe(viewLifecycleOwner){
            if (it != null || it != ""){
                if (viewModel.seeAllPaymentProcessData.value?.data?.isNotEmpty() == true){

                }else{
                    showMessage(it)
                }

            }else{
                binding.paymentProcessSeeAllError.visibility = View.GONE
            }

        }
    }
    private fun getPressesdItemIndex(index:Int){

            val id = viewModel.seeAllarray.get(index).id
            var action = id?.let {
                SeeAllPaymentProcessFragmentDirections.actionSeeAllPaymentProcessFragmentToPaymentProcessDetailsFragment(
                    it
                )
            }

            if (action != null) {
                findNavController().navigate(action)
            }
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