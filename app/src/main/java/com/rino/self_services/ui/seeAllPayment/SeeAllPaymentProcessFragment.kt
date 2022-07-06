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
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SeeAllPaymentProcessFragment : Fragment() {
    private lateinit var adapter:SeeAllPaymentProcessRVAdapter
    val viewModel: SeeAllPaymentProcessViewModel by viewModels()
    private lateinit var binding: FragmentSeeAllPaymentProcessBinding
    private lateinit var period:SeeAllRequest
    private lateinit var navSeeAll: NavSeeAll
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navSeeAll = arguments?.get("nav_see_all") as NavSeeAll
            period = SeeAllRequest("token","requests",navSeeAll.me_or_others,navSeeAll.startPeriod,navSeeAll.endPeriod,1)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSeeAllPaymentProcessBinding.inflate(inflater, container, false)
        layoutManager = LinearLayoutManager(requireContext())

        observeLoading()
        obverseError()
        handleBackBotton()
        adapter = SeeAllPaymentProcessRVAdapter(period.currentFutuer,ArrayList()){
            if (it != null) {
                getPressesdItemIndex(it)
            }

        }

        binding.paymentProcessSeeAllRv.adapter = adapter
        binding.paymentProcessSeeAllRv.layoutManager = layoutManager
        adapter.updateItems(listOf(null))
        adapter.notifyItemInserted(1)
        viewModel.getData(period)
        observeData()
        binding.paymentProcessSeeAllRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val  visibleItemCount = layoutManager.childCount
                    val  totalItemCount = layoutManager.itemCount
                    val  pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && viewModel.loading.value == View.GONE && viewModel.pageNumber < viewModel.totalPages) {
                            viewModel.pageNumber += 1
                            viewModel.getData(period)
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
    fun observeData(){
        viewModel.seeAllPaymentProcessData.observe(viewLifecycleOwner){
            it?.let {
                it.data.let { it1 -> adapter.updateItems(viewModel.seeAllarray)
                }

            }
            if (it == null){
                adapter.updateItems(listOf(null))
                adapter.notifyItemInserted(viewModel.seeAllarray.size-1)
            }
        }
    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {

                if(it == View.VISIBLE){
                    adapter.updateItems(listOf(null))
                    adapter.notifyItemInserted(viewModel.seeAllarray.size-1)
                }
                else if(it == View.GONE){
                    viewModel.seeAllarray.removeIf{ it == null }
                }
            }
        }
    }
    private fun obverseError(){
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

            val id = viewModel.seeAllarray[index]?.id
            var action = SeeAllPaymentProcessFragmentDirections.actionSeeAllPaymentProcessFragmentToPaymentProcessDetailsFragment(
                NavToDetails(navSeeAll.me_or_others,id!!,true),navSeeAll)
                findNavController().navigate(action)
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