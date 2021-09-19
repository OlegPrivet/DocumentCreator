package com.olegdev.documentcreator.adapters

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.criminalintent.adapters.baseadapter.BaseAdapter
import com.example.criminalintent.adapters.baseadapter.BaseAdapterCallback
import com.example.criminalintent.adapters.baseadapter.BaseViewHolder
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.utils.DateUtils

class PdfFilesAdapter : BaseAdapter<Document>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Document> {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rec_item, parent, false),
            mCallback = mCallback!!
        )
    }

    class ViewHolder(itemView: View, var mCallback: BaseAdapterCallback<Document>) :
        BaseViewHolder<Document>(itemView) {

        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date)
        val tv_size: TextView = itemView.findViewById(R.id.tv_size)
        val btn_more: ImageButton = itemView.findViewById(R.id.btn_more)

        override fun bind(model: Document) {
            tv_name.text = model.name
            tv_date.text =
                DateUtils.dateFormat(date = model.date_modified!!, format = "dd.MM.YY")
            tv_size.text = Formatter.formatShortFileSize(
                tv_size.context, java.lang.Long.parseLong(
                    model.size
                        ?: "0"
                )
            )
            btn_more.setOnClickListener {
                mCallback.onItemClick(model = model, btn_more)
            }
        }

    }


}