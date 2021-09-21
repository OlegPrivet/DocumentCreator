package com.olegdev.documentcreator.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.olegdev.documentcreator.models.Document

class ListDiffUtils(private val oldList: List<Document>, private val newList: List<Document>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val modelOld = oldList[oldItemPosition]
        val modelNew = newList[newItemPosition]
        return modelOld.uuid == modelNew.uuid
                && modelOld.name == modelNew.name
                && modelOld.path == modelNew.path
    }
}