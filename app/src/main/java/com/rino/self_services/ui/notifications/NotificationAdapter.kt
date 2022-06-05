package com.rino.self_services.ui.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R.drawable.read_msg
import com.rino.self_services.R.drawable.unread_msg
import com.rino.self_services.databinding.NotificationItemBinding
import com.rino.self_services.model.pojo.notifications.Data


class NotificationAdapter (private var notificationList: ArrayList<Data>, private var context: Context) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): NotificationViewHolder {
        return NotificationViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.binding.notificationBodyText.text = notificationList[position].body

        if(notificationList[position].isread == true){
            holder.binding.notificationIsReadImg.setImageResource(read_msg)
        }
        else {
            holder.binding.notificationIsReadImg.setImageResource(unread_msg)
        }



    }



    fun updateItems(newList: List<Data>) {
        notificationList.clear()
        notificationList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        notificationList.clear()
        notifyDataSetChanged()
    }
    inner class NotificationViewHolder(val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}
}