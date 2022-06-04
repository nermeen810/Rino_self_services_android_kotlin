package com.rino.self_services.ui.payment_process_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.model.pojo.AttachmentPayment

class PPAttachmentViewAdapter(private var itemList: Array<AttachmentPayment>, private val onItemClicked: (position: Int) -> Unit): RecyclerView.Adapter<PPAttachmentViewAdapter.MyViewHolder>()  {

    class MyViewHolder(view: View, private val onItemClicked: (position: Int) -> Unit):
        RecyclerView.ViewHolder(view), View.OnClickListener{

        var clearanceNumber: TextView = view.findViewById(R.id.attachment_name_pp)

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
            .inflate(R.layout.attachment_item_pp, parent, false)
        return MyViewHolder(itemView, onItemClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.clearanceNumber.text = itemList[position].name
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}