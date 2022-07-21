package com.rino.self_services.ui.seeAllHr

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.model.pojo.HRSeeAllItem
import com.rino.self_services.utils.Constants
import com.rino.self_services.utils.dateToArabic

class HRSeeAllRVAdapter(private var currentFeatuer:String, private var itemList: ArrayList<HRSeeAllItem?>, private val onItemClicked: (position: Int?) -> Unit?): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    val loadingType = 1
    val itemType = 2

    fun updateItems(itemList: List<HRSeeAllItem?>){
        if(itemList.contains(null)){
            this.itemList.addAll(itemList)
        }else {
            this.itemList.clear()
            this.itemList.addAll(itemList)
        }

        notifyDataSetChanged()
    }
    class MyViewHolder(view: View, private val onItemClicked: (position: Int?) -> Unit?):
        RecyclerView.ViewHolder(view), View.OnClickListener{

        var clearanceNumber: TextView = view.findViewById(R.id.serviceNumValue)
        var clearanceDate:TextView = view.findViewById(R.id.date_from_txt)
        var employeeNumber:TextView = view.findViewById(R.id.empNumValue)
        var employeeName:TextView = view.findViewById(R.id.empNameValue)
        var office:TextView = view.findViewById(R.id.departmentValue)
        var action:TextView = view.findViewById(R.id.request_statusValue)
        var forward:TextView = view.findViewById(R.id.request_toValue)
        var forwardTxt:TextView = view.findViewById(R.id.request_toTxt)
        var type:TextView = view.findViewById(R.id.request_typeValue)
        var startLabel:TextView = view.findViewById(R.id.vacationStartTxt)
        var endLabel:TextView = view.findViewById(R.id.vacationEndTxt)
        var start:TextView = view.findViewById(R.id.vacationStartValue)
        var end:TextView = view.findViewById(R.id.vacationEndValue)
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
                .inflate(R.layout.hr_see_all_item, parent, false)
            return MyViewHolder(itemView, onItemClicked)
        }
        else{
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.shimmer_view, parent, false)

            return LoadingAnimation(itemView)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if(itemList[position] == null){
            loadingType
        }else{
            itemType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder){
            val item = itemList[position]
            holder.clearanceNumber.text = Constants.convertNumsToArabic(item!!.id.toString())
            holder.clearanceDate.text = item.date!!.split("T")[0].dateToArabic()
            holder.employeeNumber.text = Constants.convertNumsToArabic(item.code?:"")
            holder.employeeName.text = item.employee
            holder.office.text = item.department
            holder.action.text = item.current.name
            if(item.current.users.isEmpty()){
                holder.forward.visibility = View.GONE
                holder.forwardTxt.visibility = View.GONE
            }
            else {
                holder.forward.text = item.current.users.get(0)
            }
            holder.type.text = item.type

            Log.e("startDate",(item.start != null).toString() +",,"+(currentFeatuer != "me").toString())
            if (item.start != null){
                holder.startLabel.visibility = View.VISIBLE
                holder.endLabel.visibility = View.VISIBLE
                holder.start.visibility = View.VISIBLE
                holder.end.visibility = View.VISIBLE
                holder.start.text = item.start!!.split("T")[0].dateToArabic()
                holder.end.text = item.end!!.split("T")[0].dateToArabic()
            }else{
                holder.startLabel.visibility = View.GONE
                holder.endLabel.visibility = View.GONE
                holder.start.visibility = View.GONE
                holder.end.visibility = View.GONE
            }
        }

    }
    private class LoadingAnimation(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun getItemCount(): Int {
       return itemList.size
    }


}