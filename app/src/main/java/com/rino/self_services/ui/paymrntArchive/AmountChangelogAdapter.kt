package com.rino.self_services.ui.paymrntArchive

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.databinding.PaymentArchiveItemBinding
import com.rino.self_services.model.pojo.amountChangelog.Data
import com.rino.self_services.utils.dateToArabic
import com.rino.self_services.utils.numToArabic
import kotlin.math.absoluteValue

class AmountChangelogAdapter(private var amountChangelogList: ArrayList<Data>) : RecyclerView.Adapter<AmountChangelogAdapter.AmountChangelogViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): AmountChangelogViewHolder {
        return AmountChangelogViewHolder(
            PaymentArchiveItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return amountChangelogList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AmountChangelogViewHolder, position: Int) {
        val temp = amountChangelogList[position]
        holder.binding.dateFromTxt.text = temp.createdAt.toString().split(" ")[0].dateToArabic()
        holder.binding.departmentValue.text = temp.departmentName
        holder.binding.empNameValue.text = temp.employeeName
        holder.binding.moveNumValue.text = temp.id.toString().dateToArabic()
        holder.binding.empNumValue.text = temp.employeeId.toString().dateToArabic()
        holder.binding.realAmountValue.text = temp.oldAmount.toString().dateToArabic() + " ر.س "
        holder.binding.differanceAmountValue.text = (temp.newAmount-temp.oldAmount).toString().numToArabic() + " ر.س "
        holder.binding.updateAmountValue.text = temp.newAmount.toString().dateToArabic() + " ر.س "

        if (temp.newAmount!! >= temp.oldAmount!!){
            holder.binding.differanceAmountValue.text =
                " + " +(temp.oldAmount?.minus(temp.newAmount!!))?.absoluteValue.toString()
                    .dateToArabic() + " ر.س "
        }
    else if (temp.newAmount!! >= temp.oldAmount!!){
            holder.binding.differanceAmountValue.text =
                " - "+(temp.oldAmount?.minus(temp.newAmount!!))?.absoluteValue.toString()
                    .dateToArabic() + " ر.س "
        }




    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newList: List<Data>) {
        amountChangelogList.clear()
        amountChangelogList.addAll(newList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        amountChangelogList.clear()
        notifyDataSetChanged()
    }

    inner class AmountChangelogViewHolder(val binding: PaymentArchiveItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}