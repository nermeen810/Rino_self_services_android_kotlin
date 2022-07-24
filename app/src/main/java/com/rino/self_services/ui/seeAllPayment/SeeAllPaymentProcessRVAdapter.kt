package com.rino.self_services.ui.seeAllPayment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.model.pojo.Item
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.dateToArabic


class SeeAllPaymentProcessRVAdapter(private var currentFeatuer:String,private var itemList: ArrayList<Item?>, private val onItemClicked: (position: Int?) -> Unit?): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    val loadingType = 1
    val itemType = 2
    fun updateItems(itemList: List<Item?>){
        if(itemList.contains(null)){
            this.itemList.addAll(itemList)
        }else {
            this.itemList.clear()
            this.itemList.addAll(itemList)
        }

        notifyDataSetChanged()
    }

    class MyViewHolder(view: View, private val onItemClicked: ((position: Int?) -> Unit?)?):
        RecyclerView.ViewHolder(view), View.OnClickListener{
        var orderNumber: TextView = view.findViewById(R.id.serviceNumValue)
        var side: TextView = view.findViewById(R.id.agencyValue)
        var date: TextView = view.findViewById(R.id.date_from_txt)
        var amount: TextView = view.findViewById(R.id.amountValue)
        var paymentMethod: TextView = view.findViewById(R.id.paymentMethodValue)
        var orderState: TextView = view.findViewById(R.id.request_statusValue)
        var forwerd: TextView = view.findViewById(R.id.request_toValue)

        var forwerdLabel:TextView = view.findViewById(R.id.request_toTxt)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            onItemClicked?.let { it(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == itemType){
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.see_all, parent, false)
            return MyViewHolder(itemView, onItemClicked)
        }else{
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.shimmer_view, parent, false)

            return LoadingAnimation(itemView)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (itemList[position] == null){
            return  loadingType
        }else{
            return  itemType
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder){
            val item = itemList[position]
            holder.orderNumber.text = Constants.convertNumsToArabic(item!!.id.toString())
            holder.date.text = item.date.split("T")[0].dateToArabic()
            holder.amount.text = Constants.convertNumsToArabic(item.amount?.toString()?:"")+" ر.س "
            holder.orderState.text = item.current.name
            holder.side.text = item.department
            holder.paymentMethod.text = "كاش"
            if(item.current.users.isEmpty()){
                holder.forwerd.visibility = View.GONE
                holder.forwerdLabel.visibility = View.GONE
            }
            else {
                holder.forwerd.text = item.current.users.get(0)
            }
            if (currentFeatuer == "me"){
                holder.forwerdLabel.visibility = View.GONE
                holder.forwerd.visibility = View.GONE
            }else{
                holder.forwerdLabel.visibility = View.VISIBLE
                holder.forwerd.visibility = View.VISIBLE

            }
        }

    }
    private class LoadingAnimation(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun getItemCount(): Int {
        return  itemList.size
    }
}