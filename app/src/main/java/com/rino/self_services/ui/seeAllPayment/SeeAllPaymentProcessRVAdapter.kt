package com.rino.self_services.ui.seeAllPayment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.model.pojo.Item


class SeeAllPaymentProcessRVAdapter(private var currentFeatuer:String,private var itemList: ArrayList<Item>, private val onItemClicked: (position: Int) -> Unit): RecyclerView.Adapter<SeeAllPaymentProcessRVAdapter.MyViewHolder>()  {

    fun updateItems(itemList: List<Item>){
        this.itemList.clear()
        this.itemList.addAll(itemList)

        notifyDataSetChanged()
    }
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
        var orderStateContainer:CardView = view.findViewById(R.id.order_state_container)
        var forwerdContainer:CardView = view.findViewById(R.id.forwerd_container)
        var forwerdLabel:TextView = view.findViewById(R.id.textView14)

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
        holder.date.text = item.date.split("T")[0]
        holder.amount.text = item.amount?.toString()
        holder.orderState.text = item.current.name
        holder.side.text = item.beneficiary
        holder.paymentMethod.text = "cash"
        holder.forwerd.text = item.current.users[0]
        holder.balance.text = "ر.س"+(item?.balance ?: 0.0).toString()
        if (currentFeatuer == "me"){
            holder.orderState.setBackgroundColor(Color.parseColor("#f6ac07"))
            holder.forwerdLabel.alpha = 0f
            holder.forwerdContainer.alpha = 0f
        }else{
            holder.forwerdLabel.alpha = 1f
            holder.forwerdContainer.alpha = 1f
            holder.forwerdContainer.setBackgroundColor(Color.parseColor("#f6ac07"))
            holder.orderState.setBackgroundColor(Color.parseColor("#c4c4c4"))
        }
    }

    override fun getItemCount(): Int {
        return  itemList.size
    }
}