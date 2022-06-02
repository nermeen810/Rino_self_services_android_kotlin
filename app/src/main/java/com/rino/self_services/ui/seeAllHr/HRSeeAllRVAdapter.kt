package com.rino.self_services.ui.seeAllHr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.model.pojo.HRSeeAllItem

class HRSeeAllRVAdapter(private var currentFeatuer:String, private var itemList: ArrayList<HRSeeAllItem>, private val onItemClicked: (position: Int) -> Unit): RecyclerView.Adapter<HRSeeAllRVAdapter.MyViewHolder>()  {
    fun updateItems(itemList: List<HRSeeAllItem>){
        this.itemList.addAll(itemList)

        notifyDataSetChanged()
    }
    class MyViewHolder(view: View, private val onItemClicked: (position: Int) -> Unit):
        RecyclerView.ViewHolder(view), View.OnClickListener{

        var clearanceNumber: TextView = view.findViewById(R.id.number)
        var clearanceDate:TextView = view.findViewById(R.id.clearance_date)
        var employeeNumber:TextView = view.findViewById(R.id.clearance_emp_number)
        var employeeName:TextView = view.findViewById(R.id.clearance_emp_name)
        var office:TextView = view.findViewById(R.id.clearance_office)
        var action:TextView = view.findViewById(R.id.clearance_action)
        var forward:TextView = view.findViewById(R.id.clearance_forwerd)
        var type:TextView = view.findViewById(R.id.clearance_type)
        var startLabel:TextView = view.findViewById(R.id.start_label)
        var endLabel:TextView = view.findViewById(R.id.end_label)
        var start:TextView = view.findViewById(R.id.clearance_start)
        var end:TextView = view.findViewById(R.id.clearance_end)
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
            .inflate(R.layout.hr_see_all_item, parent, false)
        return MyViewHolder(itemView, onItemClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.clearanceNumber.text = item.id.toString()
        holder.clearanceDate.text = item.date
        holder.employeeNumber.text = item.code
        holder.employeeName.text = item.employee
        holder.office.text = item.department
        holder.action.text = item.current?.name
        holder.forward.text = item.current?.users?.get(0)
        holder.type.text = item.type

        if (item.start != null && currentFeatuer != "me"){
            holder.startLabel.alpha = 1f
            holder.endLabel.alpha = 1f
            holder.start.alpha = 1f
            holder.end.alpha = 1f
            holder.start.text = item.start
            holder.end.text = item.end
        }else{
            holder.startLabel.alpha = 0f
            holder.endLabel.alpha = 0f
            holder.start.alpha = 0f
            holder.end.alpha = 0f
        }
    }

    override fun getItemCount(): Int {
       return itemList.size
    }


}