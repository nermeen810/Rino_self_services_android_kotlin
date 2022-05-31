package com.rino.self_services.ui.seeAllPayment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rino.self_services.databinding.FragmentSeeAllPaymentProcessBinding
import com.rino.self_services.model.pojo.Item
import com.rino.self_services.model.pojo.SeeAllRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeAllPaymentProcessFragment : Fragment() {
    private lateinit var adapter:SeeAllPaymentProcessRVAdapter
    val viewModel: SeeAllPaymentProcessViewModel by viewModels()
    private lateinit var binding: FragmentSeeAllPaymentProcessBinding
    private  var period = SeeAllRequest()
    private lateinit var data:List<Item>



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSeeAllPaymentProcessBinding.inflate(inflater, container, false)
        binding.paymentProcessSeeAllRv.layoutManager = LinearLayoutManager(this.context)
        adapter = SeeAllPaymentProcessRVAdapter(data){
            getPressesdItemIndex(it)
        }
        binding.paymentProcessSeeAllRv.adapter = adapter
        adapter.notifyDataSetChanged()
        viewModel.getData()
        oserveData()
        return binding.root
    }
    fun oserveData(){
        viewModel.seeAllPaymentProcessData.observe(viewLifecycleOwner){

        }
    }
    private fun getPressesdItemIndex(index:Int){

    }


}