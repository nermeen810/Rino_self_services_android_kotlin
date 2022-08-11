package com.rino.self_services.ui.managmentAlarts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.rino.self_services.databinding.MangementAlertsItemBinding
import com.rino.self_services.model.pojo.managementAlerts.ManagementAlertsResponse


class ManagementAlertsAdapter(
    private var managementAlertsList: ArrayList<ManagementAlertsResponse>,
    private var viewModel: ManagementsAlertsViewModel
) : RecyclerView.Adapter<ManagementAlertsAdapter.ManagementAlertsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): ManagementAlertsViewHolder {
        return ManagementAlertsViewHolder(
            MangementAlertsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return managementAlertsList.size
    }

    override fun onBindViewHolder(holder: ManagementAlertsViewHolder, position: Int) {
        val temp = managementAlertsList[position]
//        holder.binding.dateFromTxt.text = temp.createdAt.split("T")[0].dateToArabic()
//
//        holder.binding.serviceNumValue.text = temp.id.toString().dateToArabic()

    }

    fun updateItems(newList: List<ManagementAlertsResponse>) {
        managementAlertsList.clear()
        managementAlertsList.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearList() {
        managementAlertsList.clear()
        notifyDataSetChanged()
    }

    inner class ManagementAlertsViewHolder(val binding: MangementAlertsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}