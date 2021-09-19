package com.olegdev.documentcreator.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.olegdev.documentcreator.models.BaseFile

class ListDiffUtils<P>(private val oldList: List<P>, private val newList: List<P>) : DiffUtil.Callback() {
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
        val modelOld = oldList[oldItemPosition] as BaseFile
        val modelNew = newList[newItemPosition] as BaseFile
        return modelOld.id == modelNew.id
                && modelOld.name == modelNew.name
                && modelOld.path == modelNew.path
    }
}