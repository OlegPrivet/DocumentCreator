package com.olegdev.documentcreator.adapters

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.adapters.baseadapter.BaseAdapter
import com.olegdev.documentcreator.adapters.baseadapter.BaseViewHolder
import com.olegdev.documentcreator.utils.DateUtils
import java.io.File

class FileExplorerAdapter : BaseAdapter<File>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<File> {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rec_item, parent, false))
    }

    class ViewHolder(itemView: View) :
        BaseViewHolder<File>(itemView) {

        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date)
        val tv_size: TextView = itemView.findViewById(R.id.tv_size)
        val btn_more: ImageButton = itemView.findViewById(R.id.btn_more)
        val iv_pdf: ImageView = itemView.findViewById(R.id.iv_pdf)

        override fun bind(model: File) {
            tv_name.text = model.name
            tv_date.text =
                DateUtils.dateFormat(date = model.lastModified(), format = "dd.MM.YY")
            tv_size.text = Formatter.formatShortFileSize(
                tv_size.context, java.lang.Long.parseLong(
                    model.length().toString() ?: "0"
                )
            )
            btn_more.visibility = View.GONE
            if (model.isDirectory) {
                iv_pdf.setImageResource(R.drawable.ic_folder)
                tv_date.visibility = View.GONE
                tv_size.visibility = View.GONE
            }else {
                iv_pdf.setImageResource(R.drawable.ic_pdf)
                tv_date.visibility = View.VISIBLE
                tv_size.visibility = View.VISIBLE
            }
        }

    }
}