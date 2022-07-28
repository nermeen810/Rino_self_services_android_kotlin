package com.rino.self_services.ui.seeAllHr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import dagger.hilt.android.AndroidEntryPoint
import com.rino.self_services.databinding.FragmentSeeAllHrClearanceBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll


@AndroidEntryPoint
class SeeAllHrClearanceFragment : Fragment() {

    val viewModel: SeeAllHRClearanceViewModel by viewModels()
    private lateinit var adapter:HRSeeAllRVAdapter
    private lateinit var period: HRClearanceRequest
    private lateinit var binding: FragmentSeeAllHrClearanceBinding
    private lateinit var v:NavSeeAll
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
             v = arguments?.get("nav_to_see_all_clearance") as NavSeeAll
            period = HRClearanceRequest(v.startPeriod,v.endPeriod,v.me_or_others,1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSeeAllHrClearanceBinding.inflate(inflater, container, false)
        layoutManager = LinearLayoutManager(requireContext())
        observeLoading()
        oserveData()
        observeError()
        handleBack()
        adapter = HRSeeAllRVAdapter(period.meOrOthers,ArrayList()){
            if (it != null) {
                getPressesdItemIndex(it)
            }
        }

        binding.hrClearanceSeeAllRv.adapter = adapter
        binding.hrClearanceSeeAllRv.layoutManager = layoutManager
        viewModel.getData(period)
        binding.hrClearanceSeeAllRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun handleBack() {
        binding.backbtn.setOnClickListener {
            val action =
                SeeAllHrClearanceFragmentDirections.seeAllHrClearanceFragmentToHrClearanceHome()
            findNavController().navigate(action)
        }
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {

                if(it == View.VISIBLE){
                    adapter.updateItems(listOf(null))
                    adapter.notifyItemInserted(viewModel.arrayList.size-1)
                }
                else if(it == View.GONE){
                    viewModel.arrayList.removeIf{ it == null }
                }
            }
        }
    }
    fun oserveData(){
        viewModel.seeAllPaymentProcessData.observe(viewLifecycleOwner){
            it?.let {
                it.data.let { it1 -> adapter.updateItems(viewModel.arrayList)
                }

            }
//            if (it == null){
//                adapter.updateItems(listOf(null))
//                adapter.notifyItemInserted(viewModel.arrayList.size-1)
//            }
            }

    }
    private fun observeError(){
        viewModel.setError.observe(viewLifecycleOwner){
            if (it != null || it != ""){
                if (viewModel.seeAllPaymentProcessData.value?.data?.isNotEmpty() == true){

                }else{
                    showMessage(it)
//                    binding.seeAllClearanceError.text = it
//                    binding.seeAllClearanceError.visibility = View.VISIBLE
                }

            }else{
                binding.seeAllClearanceError.visibility = View.GONE
            }

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
    private fun getPressesdItemIndex(index:Int){
            val id = viewModel.arrayList[index].id
            val entity = viewModel.arrayList[index].entity
            var action = SeeAllHrClearanceFragmentDirections.actionSeeAllHrClearanceFragmentToHRClearanceDetailsFragment(

                HRClearanceDetailsRequest(entity!!,id?:-1,true,v.me_or_others), v )
            findNavController().navigate(action)


    }
}