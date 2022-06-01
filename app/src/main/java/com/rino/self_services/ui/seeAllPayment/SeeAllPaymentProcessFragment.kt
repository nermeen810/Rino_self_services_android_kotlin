package com.rino.self_services.ui.seeAllPayment

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.databinding.FragmentSeeAllPaymentProcessBinding

import com.rino.self_services.model.pojo.SeeAllRequest
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SeeAllPaymentProcessFragment : Fragment() {
    private lateinit var adapter:SeeAllPaymentProcessRVAdapter
    val viewModel: SeeAllPaymentProcessViewModel by viewModels()
    private lateinit var binding: FragmentSeeAllPaymentProcessBinding
    private var period = SeeAllRequest("token","requests","me","2020-01-01","2020-12-30",1)
    private var totalPages = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSeeAllPaymentProcessBinding.inflate(inflater, container, false)

        observeLoading()
        overseError()

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
    fun oserveData(){
        viewModel.seeAllPaymentProcessData.observe(viewLifecycleOwner){
            it?.let {
                totalPages = it.totalPages
                it.data.let { it1 -> adapter.updateItems(it1)
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
                binding.paymentProcessSeeAllError.text = it
                binding.paymentProcessSeeAllError.visibility = View.VISIBLE
            }else{
                binding.paymentProcessSeeAllError.visibility = View.GONE
            }

        }
    }
    private fun getPressesdItemIndex(index:Int){

    }


}