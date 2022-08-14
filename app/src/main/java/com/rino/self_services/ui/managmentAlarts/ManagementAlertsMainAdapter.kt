package com.rino.self_services.ui.managmentAlarts

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.databinding.PaymentHomeItemBinding
import com.rino.self_services.model.pojo.managementAlerts.Data
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.dateToArabic

class ManagementAlertsMainAdapter (private var managementAlertsList: ArrayList<Data>,
                                   private val ViewModel: ManagementsAlertsViewModel, private val context: Context
) : RecyclerView.Adapter<ManagementAlertsMainAdapter.ManagementAlertsMainViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): ManagementAlertsMainViewHolder {
        return ManagementAlertsMainViewHolder(
            PaymentHomeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return managementAlertsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ManagementAlertsMainViewHolder, position: Int) {
        val temp = managementAlertsList[position]
        holder.binding.periodTxt.text = temp.title?.dateToArabic() ?: ""
        holder.binding.requestNumTxt.text =
            Constants.convertNumsToArabic("(" + temp.count.toString()) + " " + context.getString(
                R.string.task
            ) + ")"

        holder.binding.historyRecycle.visibility = View.VISIBLE
        var historyAdapter = ManagementAlertsSubAdapter(arrayListOf(), ViewModel, context)
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.stackFromEnd = true
        holder.binding.historyRecycle.apply {
            layoutManager = linearLayoutManager
            adapter = historyAdapter
        }
        historyAdapter.updateItems(temp.items)
        holder.binding.showAllTxt.setOnClickListener {
//            hrClearanceViewModel.navToSeeAll(
//                NavSeeAll(
//                    hrClearanceViewModel.me_or_others, temp.start.toString(),
//                    temp.end.toString()
//                )
//            )
        }
        holder.binding.showAllBtn.setOnClickListener {
//            hrClearanceViewModel.navToSeeAll(
//                NavSeeAll(
//                    hrClearanceViewModel.me_or_others, temp.start.toString(),
//                    temp.end.toString()
//                )
//            )
        }
        holder.binding.card.setOnClickListener {
//            hrClearanceViewModel.navToSeeAll(
//                NavSeeAll(
//                    hrClearanceViewModel.me_or_others, temp.start.toString(),
//                    temp.end.toString()
//                )
//            )
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newList: List<Data>) {
        managementAlertsList.clear()
        managementAlertsList.addAll(newList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        managementAlertsList.clear()
        notifyDataSetChanged()
    }

    inner class ManagementAlertsMainViewHolder(val binding: PaymentHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}