package com.rino.self_services.ui.seeAllPayment

import android.graphics.Color
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.utils.Constants
import com.rino.self_services.R
import com.rino.self_services.di.root.RootApplication_HiltComponents
import com.rino.self_services.model.pojo.Item


class SeeAllPaymentProcessRVAdapter(private var currentFeatuer:String,private var itemList: ArrayList<Item>, private val onItemClicked: (position: Int) -> Unit): RecyclerView.Adapter<SeeAllPaymentProcessRVAdapter.MyViewHolder>()  {

    fun updateItems(itemList: List<Item>){
        this.itemList.clear()
        this.itemList.addAll(itemList)

        notifyDataSetChanged()
    }
    class MyViewHolder(view: View, private val onItemClicked: (position: Int) -> Unit):
        RecyclerView.ViewHolder(view), View.OnClickListener{
        var orderNumber: TextView = view.findViewById(R.id.serviceNumValue)
        var side: TextView = view.findViewById(R.id.agencyValue)
        var date: TextView = view.findViewById(R.id.date_from_txt)
        var amount: TextView = view.findViewById(R.id.amountValue)
     //   var balance: TextView = view.findViewById(R.id.agencyValue)
        var paymentMethod: TextView = view.findViewById(R.id.paymentMethodValue)
        var orderState: TextView = view.findViewById(R.id.request_statusValue)
        var forwerd: TextView = view.findViewById(R.id.request_toValue)
 //       var forwerdContainer:CardView = view.findViewById(R.id.forwerd_container)
        var forwerdLabel:TextView = view.findViewById(R.id.request_toTxt)

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
        holder.orderNumber.text = Constants.convertNumsToArabic(item.id.toString())
        holder.date.text = item.date.split("T")[0]
        holder.amount.text = Constants.convertNumsToArabic(item.amount?.toString()?:"")+" ر.س "
        holder.orderState.text = item.current.name
        holder.side.text = item.department
        holder.paymentMethod.text = "كاش"
        holder.forwerd.text = item.current.users[0]
   //     holder.balance.text = "ر.س"+(item?.balance ?: 0.0).toString()
        if (currentFeatuer == "me"){
            holder.forwerdLabel.visibility = View.GONE
            holder.forwerd.visibility = View.GONE
        }else{
            holder.forwerdLabel.visibility = View.VISIBLE
            holder.forwerd.visibility = View.VISIBLE

        }
    }

    override fun getItemCount(): Int {
        return  itemList.size
    }
}