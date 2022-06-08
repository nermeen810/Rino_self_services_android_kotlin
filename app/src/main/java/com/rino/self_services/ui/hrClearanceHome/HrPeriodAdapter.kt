package com.rino.self_services.ui.hrClearanceHome

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.databinding.PeriodItemBinding

class HrPeriodAdapter (private var periodList: ArrayList<String>,private val hrClearanceHomeViewModel: HrClearanceViewModel) : RecyclerView.Adapter<HrPeriodAdapter.PeriodViewHolder>() {

    var lastSelectedCard: CardView? = null
    var lastSelectedText: TextView? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): PeriodViewHolder {
        return PeriodViewHolder(
            PeriodItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: PeriodViewHolder, position: Int) {
        // Log.e("lastPosition",FilteredHistoryViewModel.lastSelectedPos.toString())

        if (lastSelectedCard == null && position == HrClearanceViewModel.lastSelectedPos) {
            Log.e(
                "period",
                HrClearanceViewModel.periodTimeList_en[HrClearanceViewModel.lastSelectedPos]
            )

            lastSelectedCard = holder.binding.categoryCard
            lastSelectedText = holder.binding.categoryName
            //     filteredHistoryViewModel.getHistoryData(filteredHistoryViewModel.serviceId,FilteredHistoryViewModel.periodTimeList_en[FilteredHistoryViewModel.lastSelectedPos])

            lastSelectedCard?.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.categoryCard.context,
                    R.color.color_orange
                )
            )
            lastSelectedText?.setTextColor(
                ContextCompat.getColor(
                    holder.binding.categoryCard.context,
                    R.color.white
                )
            )
        }
        val temp = periodList[position]
        holder.bind(temp, position)
    }

    inner class PeriodViewHolder(val binding: PeriodItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(period: String, position: Int) {

            binding.categoryName.text = period
            binding.categoryCard.setOnClickListener {
                HrClearanceViewModel.lastSelectedPos = position
                Log.e("lastPos", HrClearanceViewModel.lastSelectedPos.toString())
                hrClearanceHomeViewModel.getPaymentData(hrClearanceHomeViewModel.me_or_others)
                lastSelectedCard?.setCardBackgroundColor(
                    ContextCompat.getColor(
                        it.context,
                        R.color.white
                    )
                )
                lastSelectedText?.setTextColor(
                    ContextCompat.getColor(
                        it.context,
                        R.color.black
                    )
                )

                binding.categoryCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        it.context,
                        R.color.color_orange
                    )
                )
                binding.categoryName.setTextColor(
                    ContextCompat.getColor(
                        it.context,
                        R.color.white
                    )
                )
                lastSelectedCard = binding.categoryCard
                lastSelectedText = binding.categoryName
            }
        }
    }

    override fun getItemCount(): Int {
        return periodList.size
    }
}