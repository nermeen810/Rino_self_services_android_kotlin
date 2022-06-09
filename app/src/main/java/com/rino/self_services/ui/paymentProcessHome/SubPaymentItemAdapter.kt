package com.rino.self_services.ui.paymentProcessHome

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.databinding.PaymentHistoryItemBinding
import com.rino.self_services.model.pojo.payment.Items
import com.rino.self_services.utils.Constants


class SubPaymentItemAdapter (private var paymentSubList: ArrayList<Items>,
                             private val paymentHomeViewModel: PaymentHomeViewModel, private val context: Context
) : RecyclerView.Adapter<SubPaymentItemAdapter.SubPaymentItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): SubPaymentItemViewHolder {
        return SubPaymentItemViewHolder(
            PaymentHistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return paymentSubList.size
    }

    override fun onBindViewHolder(holder: SubPaymentItemViewHolder, position: Int) {
        holder.binding.serviceNumValue.text = Constants.convertNumsToArabic(paymentSubList[position].id.toString())
        val dateRes = paymentSubList[position].date.split("T")
        holder.binding.dateFromTxt.text     = Constants.convertNumsToArabic(dateRes[0])
        holder.binding.amountValue.text     = Constants.convertNumsToArabic(paymentSubList[position].amount.toString())
        holder.binding.agencyValue.text     = Constants.convertNumsToArabic(paymentSubList[position].department?:"")
        holder.binding.paymentMethodValue.text     = context.getText(R.string.cash)
        holder.binding.requestStatusValue.text     = paymentSubList[position].status?:""
        if(paymentHomeViewModel.me_or_others=="others") {
            holder.binding.requestToValue.text = paymentSubList[position].current?.users?.get(0)
        }
        else if(paymentHomeViewModel.me_or_others=="me")
        {
            holder.binding.requestToValue.visibility = View.GONE
            holder.binding.requestToTxt.visibility = View.GONE
        }
        //      holder.binding.timeTxt.text         = historyList[position].createdDate?: "00/00/00 00:00".split(" ").toList()[1]
        holder.binding.card.setOnClickListener {
            Log.i("id",paymentSubList[position].id.toString()?:"")
            paymentHomeViewModel.navToServiceDetails(NavToDetails(PaymentHomeViewModel.me_or_others,paymentSubList[position].id!!,true))
//            paymentSubList[position].id?.let { it1 -> paymentHomeViewModel.navToServiceDetails(it1) }
        }

    }


    fun updateItems(newList: List<Items>) {
        paymentSubList.clear()
        paymentSubList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        paymentSubList.clear()
        notifyDataSetChanged()
    }
    inner class SubPaymentItemViewHolder(val binding: PaymentHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}


