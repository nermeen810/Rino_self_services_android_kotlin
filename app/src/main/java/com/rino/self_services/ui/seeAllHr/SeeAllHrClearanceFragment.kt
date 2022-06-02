package com.rino.self_services.ui.seeAllHr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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




@AndroidEntryPoint
class SeeAllHrClearanceFragment : Fragment() {

    val viewModel: SeeAllHRClearanceViewModel by viewModels()
    private lateinit var adapter:HRSeeAllRVAdapter
    private lateinit var period: HRClearanceRequest
    private lateinit var binding: FragmentSeeAllHrClearanceBinding
    private var totalPages = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            period = HRClearanceRequest("2021-01-01","2021-12-31","me",1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSeeAllHrClearanceBinding.inflate(inflater, container, false)
        observeLoading()
        oserveData()
        overseError()
        adapter = HRSeeAllRVAdapter(period.meOrOthers,ArrayList()){
            getPressesdItemIndex(it)
        }

        binding.hrClearanceSeeAllRv.adapter = adapter
        binding.hrClearanceSeeAllRv.layoutManager = LinearLayoutManager(this.context)
        viewModel.getData(period)
        binding.hrClearanceSeeAllRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    if (viewModel.pageNumber < totalPages?:1 && viewModel.loading.value == View.GONE){
                        viewModel.pageNumber += 1
                        viewModel.getData(period)
                    }
                }
            }
        })
        return binding.root
    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.clearanceSeeAllProgress.visibility = it
            }
        }
    }
    fun oserveData(){
        viewModel.seeAllPaymentProcessData.observe(viewLifecycleOwner){
            it?.let {


                it.let { it1 -> adapter.updateItems(viewModel.arrayList)
                }
            }
        }
    }
    private fun overseError(){
        viewModel.setError.observe(viewLifecycleOwner){
            if (it != null || it != ""){
                if (viewModel.seeAllPaymentProcessData.value?.date?.isNotEmpty() == true){

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


            val id = viewModel.arrayList.get(index).id
            val entity = viewModel.arrayList.get(index).entity
            var action = SeeAllHrClearanceFragmentDirections.actionSeeAllHrClearanceFragmentToHRClearanceDetailsFragment(
                HRClearanceDetailsRequest(entity!!,id)
            )
            findNavController().navigate(action)


    }
}