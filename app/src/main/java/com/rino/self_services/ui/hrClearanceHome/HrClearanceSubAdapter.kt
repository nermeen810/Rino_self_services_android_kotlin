package com.rino.self_services.ui.hrClearanceHome

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.databinding.HrClearanceHomeItemBinding
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.model.pojo.hrClearance.Items
import com.rino.self_services.ui.paymentProcessHome.PaymentHomeViewModel
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.dateToArabic

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
        val dateRes = clearanceSubList[position].date!!.split("T")
        holder.binding.dateFromTxt.text     = dateRes[0].dateToArabic()
        holder.binding.empNumValue.text     = Constants.convertNumsToArabic(clearanceSubList[position].code.toString())
        holder.binding.empNameValue.text     = clearanceSubList[position].employee
        holder.binding.departmentValue.text     = clearanceSubList[position].department
        holder.binding.requestStatusValue.text     = clearanceSubList[position].status?:""
        holder.binding.requestTypeValue.text    = clearanceSubList[position].type
        if(clearanceSubList[position].start!=null) {
            val startDateRes = clearanceSubList[position].start!!.split("T")
            holder.binding.vacationStartValue.text = startDateRes[0].dateToArabic()
        }
        else{
            holder.binding.vacationStartValue.visibility = View.GONE
            holder.binding.vacationStartTxt.visibility =View.GONE
        }
        if(clearanceSubList[position].end!=null) {
            val endDateRes = clearanceSubList[position].end!!.split("T")
            holder.binding.vacationEndValue.text = endDateRes[0].dateToArabic()
        }
        else{
            holder.binding.vacationEndValue.visibility = View.GONE
            holder.binding.vacationEndTxt.visibility =View.GONE
        }
        if(hrClearanceViewModel.me_or_others=="others") {
            holder.binding.requestToValue.text = clearanceSubList[position].current.users.get(0)
            if(clearanceSubList[position].current.users.size ==0){
                holder.binding.requestToValue.visibility = View.GONE
                holder.binding.requestToTxt.visibility = View.GONE
            }
            else {
                holder.binding.requestToValue.text = clearanceSubList[position].current.users.get(0)
            }
        }
        else if(hrClearanceViewModel.me_or_others=="me")
        {
            holder.binding.requestToValue.visibility = View.GONE
            holder.binding.requestToTxt.visibility = View.GONE
        }

        var meOrOthers = ""

        if(clearanceSubList[position].me == true)
        {
            meOrOthers ="me"
        }
        else{
            meOrOthers = "others"
        }
        //      holder.binding.timeTxt.text         = historyList[position].createdDate?: "00/00/00 00:00".split(" ").toList()[1]
        holder.binding.card.setOnClickListener {
            Log.e("me",clearanceSubList[position].me.toString())

            hrClearanceViewModel.navToServiceDetails(
                HRClearanceDetailsRequest(clearanceSubList[position].entity?:-1,clearanceSubList[position].id?:0,true,meOrOthers)
                )
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
