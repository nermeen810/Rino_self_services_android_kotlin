package com.rino.self_services.ui.managmentAlarts

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.rino.self_services.databinding.MangementAlertsItemBinding
import com.rino.self_services.model.pojo.managementAlerts.Data
import com.rino.self_services.model.pojo.managementAlerts.Items
import com.rino.self_services.utils.dateToArabic
import com.rino.self_services.utils.numToArabic


class ManagementAlertsSubAdapter(
    private var managementAlertsList: ArrayList<Items>,
    private var viewModel: ManagementsAlertsViewModel,
    private val context: Context
) : RecyclerView.Adapter<ManagementAlertsSubAdapter.ManagementAlertsViewHolder>() {
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ManagementAlertsViewHolder, position: Int) {
        val temp = managementAlertsList[position]
        holder.binding.dateFromTxt.text = temp.statusDate.split("T")[0].dateToArabic()
//
        holder.binding.serviceNumValue.text = temp.id.toString().numToArabic()
        holder.binding.amountValue.text = temp.customData?.amount.toString().numToArabic()+" ر.س "
        holder.binding.requestToValue.text = temp.customData?.beneficiary?.numToArabic()
        holder.binding.requestStatusValue.text = temp.customData?.details?.numToArabic()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newList: List<Items>) {
        managementAlertsList.clear()
        managementAlertsList.addAll(newList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        managementAlertsList.clear()
        notifyDataSetChanged()
    }

    inner class ManagementAlertsViewHolder(val binding: MangementAlertsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}