package com.rino.self_services.ui.complaints

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.rino.self_services.R
import com.rino.self_services.databinding.PreviewAttachmentItemBinding
import java.io.File


class PreviewAttachmentAdapter (private var attachmentList: ArrayList<File>, private var context: Context,private  var viewModel: ComplaintsViewModel) : RecyclerView.Adapter<PreviewAttachmentAdapter.AttatchmentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int

    ): AttatchmentViewHolder {
        return AttatchmentViewHolder(
            PreviewAttachmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return attachmentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: AttatchmentViewHolder, position: Int) {
        holder.binding.attachmentName.text = attachmentList[position].name

        holder.binding.item.setOnClickListener {
        // openAttachment(attachmentList[position],getMimeType(attachmentList[position]))
          //  val mime =getMimeType(attachmentList[position])
//            val url = attachmentList[position].name
//            if(url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")||url.contains(".pdf"))
//            {
                viewModel.setNavToPdfPreview(attachmentList[position])
        //           }

        }
        holder.binding.cancelBtn.setOnClickListener {
             customTwoButtonsDialog(attachmentList[position])

        }

    }
    fun customTwoButtonsDialog(file: File) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)

        builder.setMessage(R.string.dialogMessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setNegativeButton(R.string.NoMessage) { dialogInterface, which ->

        }

        builder.setPositiveButton(R.string.yesMessage) { dialogInterface, which ->
            viewModel.setAttachmentsDeleteItem(file)
            attachmentList.remove(file)
            notifyDataSetChanged()

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun openAttachment(file:File,filetype:String?) {
//        val myMime = MimeTypeMap.getSingleton()
//        val newIntent = Intent(Intent.ACTION_GET_CONTENT)
//        val mimeType = myMime.getMimeTypeFromExtension(fileExt(item.name)?.substring(1))
//        newIntent.setDataAndType(FileProvider.getUriForFile(context,"com.rino.self_services.provider",item), mimeType)
//        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        try {
//            context.startActivity(newIntent)
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show()
//        }

        // sol 2
//        val myMime = MimeTypeMap.getSingleton()
//        val intent = Intent(Intent.ACTION_VIEW)
//        val mimeType = myMime.getMimeTypeFromExtension(filetype)
//        if (Build.VERSION.SDK_INT >= 24) {
//            val fileURI = FileProvider.getUriForFile(
//                context, BuildConfig.APPLICATION_ID.toString() + ".provider",
//                file
//            )
//            intent.setDataAndType(fileURI, mimeType)
//        } else {
//            intent.setDataAndType(Uri.fromFile(file), mimeType)
//        }
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
//        try {
//            context.startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(
//                context,
//                "No Application found to open this type of file.",
//                Toast.LENGTH_LONG
//            ).show()
//        }
    }
    fun getMimeType(file: File): String? {
        val uri = Uri.fromFile(file)
        val cR = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    fun updateItems(newList: List<File>) {
        attachmentList.clear()
        attachmentList.addAll(newList)
        notifyDataSetChanged()
    }
    fun clearList() {
        attachmentList.clear()
        notifyDataSetChanged()
    }
    inner class AttatchmentViewHolder(val binding: PreviewAttachmentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}
