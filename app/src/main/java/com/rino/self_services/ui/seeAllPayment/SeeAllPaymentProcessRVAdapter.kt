package com.rino.self_services.ui.seeAllPayment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.model.pojo.Item


class SeeAllPaymentProcessRVAdapter(private var itemList: List<Item>, private val onItemClicked: (position: Int) -> Unit): RecyclerView.Adapter<SeeAllPaymentProcessRVAdapter.MyViewHolder>()  {
    class MyViewHolder(view: View, private val onItemClicked: (position: Int) -> Unit):
        RecyclerView.ViewHolder(view), View.OnClickListener{
        var orderNumber: TextView = view.findViewById(R.id.order_number)
        var side: TextView = view.findViewById(R.id.order_side)
        var date: TextView = view.findViewById(R.id.order_date)
        var amount: TextView = view.findViewById(R.id.amount_see_all)
        var balance: TextView = view.findViewById(R.id.balance_see_all)
        var paymentMethod: TextView = view.findViewById(R.id.payment_method_see_all)
        var orderState: TextView = view.findViewById(R.id.order_state_see_all)
        var forwerd: TextView = view.findViewById(R.id.order_forwerd_see_all)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.see_all, parent, false)
        return MyViewHolder(itemView, onItemClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.orderNumber.text = item.id.toString()
        holder.date.text = item.date
        holder.amount.text = item.amount.toString()
        holder.balance.text = item.balance.toString()
        holder.orderState.text = item.current.name
        holder.side.text = item.beneficiary
        holder.paymentMethod.text = "cash"
        holder.forwerd.text = item.current.users[0]
    }

    override fun getItemCount(): Int {
        return  itemList.size
    }
}