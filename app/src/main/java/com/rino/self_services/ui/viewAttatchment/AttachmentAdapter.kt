package com.rino.self_services.ui.viewAttatchment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.databinding.AttatchmentItemBinding
import com.rino.self_services.model.pojo.Attachment
import com.rino.self_services.model.pojo.complaints.Attachments


class AttachmentAdapter (private var attachmentList: ArrayList<Attachments>, private var context: Context) : RecyclerView.Adapter<AttachmentAdapter.AttatchmentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): AttatchmentViewHolder {
        return AttatchmentViewHolder(
            AttatchmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return attachmentList.size
    }

    override fun onBindViewHolder(holder: AttatchmentViewHolder, position: Int) {
        holder.binding.attachmentName.text = attachmentList[position].name

        holder.binding.attachmentName.setOnClickListener {
          openAttachment(position)
        }

    }

    private fun openAttachment(pos:Int) {

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(attachmentList[pos].url))
        context.startActivity(browserIntent)
    }

    fun updateItems(newList: List<Attachments>) {
        attachmentList.clear()
        attachmentList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        attachmentList.clear()
        notifyDataSetChanged()
    }
    inner class AttatchmentViewHolder(val binding: AttatchmentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}
