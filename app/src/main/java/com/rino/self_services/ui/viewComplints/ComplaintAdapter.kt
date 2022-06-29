package com.rino.self_services.ui.viewComplints


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.databinding.ComplaintItemBinding
import com.rino.self_services.model.pojo.complaints.ComplaintItemResponse
import com.rino.self_services.utils.dateToArabic


class ComplaintAdapter (private var complaintsList: ArrayList<ComplaintItemResponse>,private  var viewModel:ViewComplaintsViewModel) : RecyclerView.Adapter<ComplaintAdapter.ComplaintsViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int

        ): ComplaintsViewHolder {
            return ComplaintsViewHolder(
                ComplaintItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return complaintsList.size
        }

        override fun onBindViewHolder(holder: ComplaintsViewHolder, position: Int) {
            val temp = complaintsList[position]
            holder.binding.dateFromTxt.text = temp.createdAt.split("T")[0].dateToArabic()
            holder.binding.adminValue.text = temp.officer
            holder.binding.departmentValue.text = temp.department
            holder.binding.descriptionValue.text = temp.body
            holder.binding.serviceNumValue.text = temp.id.toString().dateToArabic()
            holder.binding.viewAttachment.setOnClickListener{
           //     viewModel.navToAttachments(temp.attchements)
            }
        }

        fun updateItems(newList: List<ComplaintItemResponse>) {
            complaintsList.clear()
            complaintsList.addAll(newList)
            notifyDataSetChanged()
        }

        fun clearList() {
            complaintsList.clear()
            notifyDataSetChanged()
        }

        inner class ComplaintsViewHolder(val binding: ComplaintItemBinding) :
            RecyclerView.ViewHolder(binding.root)

    }