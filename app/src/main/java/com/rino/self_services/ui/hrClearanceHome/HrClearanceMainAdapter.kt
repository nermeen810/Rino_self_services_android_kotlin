package com.rino.self_services.ui.hrClearanceHome

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.databinding.PaymentHomeItemBinding
import com.rino.self_services.model.pojo.hrClearance.Data
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.PaymentHomeViewModel
import com.rino.self_services.utils.Constants

class HrClearanceMainAdapter (private var filteredHistoryList: ArrayList<Data>,
                              private val hrClearanceViewModel: HrClearanceViewModel, private val context: Context
)
    : RecyclerView.Adapter<HrClearanceMainAdapter.HrClearanceMainViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): HrClearanceMainViewHolder {
        return HrClearanceMainViewHolder(
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
    override fun onBindViewHolder(holder: HrClearanceMainViewHolder, position: Int) {
        val temp = filteredHistoryList[position]
        holder.binding.periodTxt.text = Constants.convertNumsToArabic(temp.title?:"")
        holder.binding.requestNumTxt.text = Constants.convertNumsToArabic("("+temp.count.toString())+" "+context.getString(
            R.string.task)+")"

        holder.binding.historyRecycle.visibility = View.VISIBLE
        var historyAdapter = HrClearanceSubAdapter(arrayListOf(),hrClearanceViewModel,context)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        linearLayoutManager.stackFromEnd = true
        holder.binding.historyRecycle.apply {
            layoutManager = linearLayoutManager
            adapter = historyAdapter
        }
        historyAdapter.updateItems(temp.items)
        holder.binding.showAllTxt.setOnClickListener {
            hrClearanceViewModel.navToSeeAll(
                NavSeeAll(
                    hrClearanceViewModel.me_or_others, temp.start.toString(),
                temp.end.toString())
            )
        }
        holder.binding.showAllBtn.setOnClickListener {
            hrClearanceViewModel.navToSeeAll(
                NavSeeAll(
                    hrClearanceViewModel.me_or_others, temp.start.toString(),
                temp.end.toString())
            )
        }
        holder.binding.card.setOnClickListener {
            hrClearanceViewModel.navToSeeAll(
                NavSeeAll(
                    hrClearanceViewModel.me_or_others, temp.start.toString(),
                temp.end.toString())
            )
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
    inner class HrClearanceMainViewHolder(val binding: PaymentHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root)



}