package com.rino.self_services.ui.paymentProcessHome

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.databinding.PaymentHomeItemBinding
import com.rino.self_services.model.pojo.payment.Data
import com.rino.self_services.model.pojo.payment.PaymentHomeResponse
import com.rino.self_services.utils.Constants

class PaymentMainItemAdapter (private var filteredHistoryList: ArrayList<Data>,
                              private val paymentHomeViewModel: PaymentHomeViewModel,private val context: Context)
                              : RecyclerView.Adapter<PaymentMainItemAdapter.PaymentMainItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): PaymentMainItemViewHolder {
        return PaymentMainItemViewHolder(
            PaymentHomeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filteredHistoryList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PaymentMainItemViewHolder, position: Int) {
        val temp = filteredHistoryList[position]
        holder.binding.periodTxt.text = Constants.convertNumsToArabic(temp.title?:"")
        holder.binding.requestNumTxt.text = Constants.convertNumsToArabic("("+temp.count.toString())+" "+context.getString(
            R.string.task)+")"

        holder.binding.historyRecycle.visibility = View.VISIBLE
        var historyAdapter = SubPaymentItemAdapter(arrayListOf(),paymentHomeViewModel,context)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        linearLayoutManager.stackFromEnd = true
        holder.binding.historyRecycle.apply {
            layoutManager = linearLayoutManager
            adapter = historyAdapter
        }
        historyAdapter.updateItems(temp.items)
        holder.binding.showAllTxt.setOnClickListener {
            paymentHomeViewModel.navToSeeAll(NavSeeAll(paymentHomeViewModel.me_or_others, temp.startPeriod.toString(),
                temp.endPeriod.toString()))
        }
        holder.binding.showAllBtn.setOnClickListener {
            paymentHomeViewModel.navToSeeAll(NavSeeAll(paymentHomeViewModel.me_or_others, temp.startPeriod.toString(),
                temp.endPeriod.toString()))
            }
        holder.binding.card.setOnClickListener {
            paymentHomeViewModel.navToSeeAll(NavSeeAll(paymentHomeViewModel.me_or_others, temp.startPeriod.toString(),
                temp.endPeriod.toString()))
                    }

    }

    fun updateItems(newList: List<Data>) {
        filteredHistoryList.clear()
        filteredHistoryList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        filteredHistoryList.clear()
        notifyDataSetChanged()
    }
    inner class PaymentMainItemViewHolder(val binding: PaymentHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}