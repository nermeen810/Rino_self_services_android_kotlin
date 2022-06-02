package com.rino.self_services.ui.hrClearanceHome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.databinding.HrClearanceHomeItemBinding
import com.rino.self_services.model.pojo.hrClearance.Items
import com.rino.self_services.ui.paymentProcessHome.PaymentHomeViewModel
import com.rino.self_services.utils.Constants

class HrClearanceSubAdapter (private var clearanceSubList: ArrayList<Items>,
                             private val hrClearanceViewModel: HrClearanceViewModel, private val context: Context
) : RecyclerView.Adapter<HrClearanceSubAdapter.SubHrClearanceItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): SubHrClearanceItemViewHolder {
        return SubHrClearanceItemViewHolder(
            HrClearanceHomeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return clearanceSubList.size
    }

    override fun onBindViewHolder(holder: SubHrClearanceItemViewHolder, position: Int) {
        holder.binding.serviceNumValue.text = Constants.convertNumsToArabic(clearanceSubList[position].id.toString())
        val dateRes = clearanceSubList[position].date.split("T")
        holder.binding.dateFromTxt.text     = Constants.convertNumsToArabic(dateRes[0])
        holder.binding.empNumValue.text     = Constants.convertNumsToArabic(clearanceSubList[position].code.toString())
        holder.binding.empNameValue.text     = clearanceSubList[position].employee
        holder.binding.departmentValue.text     = clearanceSubList[position].department
        holder.binding.requestStatusValue.text     = clearanceSubList[position].status?:""
        holder.binding.requestTypeValue.text    = clearanceSubList[position].type
        if(clearanceSubList[position].start!=null) {
            val startDateRes = clearanceSubList[position].start!!.split("T")
            holder.binding.vacationStartValue.text = Constants.convertNumsToArabic(startDateRes[0])
        }
        else{
            holder.binding.vacationStartValue.visibility = View.GONE
            holder.binding.vacationStartTxt.visibility =View.GONE
        }
        if(clearanceSubList[position].end!=null) {
            val endDateRes = clearanceSubList[position].end!!.split("T")
            holder.binding.vacationEndValue.text = Constants.convertNumsToArabic(endDateRes[0])
        }
        else{
            holder.binding.vacationEndValue.visibility = View.GONE
            holder.binding.vacationEndTxt.visibility =View.GONE
        }
        if(PaymentHomeViewModel.me_or_others=="others") {
            holder.binding.requestToValue.text = clearanceSubList[position].current?.users?.get(0)
        }
        else if(PaymentHomeViewModel.me_or_others=="me")
        {
            holder.binding.requestToValue.visibility = View.GONE
            holder.binding.requestToTxt.visibility = View.GONE
        }
        //      holder.binding.timeTxt.text         = historyList[position].createdDate?: "00/00/00 00:00".split(" ").toList()[1]
        holder.binding.card.setOnClickListener {
            //          paymentHomeViewModel.navToServiceDetails(historyList[position])
        }

    }

    fun updateItems(newList: List<Items>) {
        clearanceSubList.clear()
        clearanceSubList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        clearanceSubList.clear()
        notifyDataSetChanged()
    }
    inner class SubHrClearanceItemViewHolder(val binding: HrClearanceHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}
