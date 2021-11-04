package com.olegdev.documentcreator.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.olegdev.documentcreator.models.Document
import java.io.File

class ListDiffUtils<T>(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {
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
        when (oldList[oldItemPosition]) {
                is Document -> {
                    val modelOld : Document = oldList[oldItemPosition] as Document
                    val modelNew : Document  = newList[newItemPosition] as Document
                    return modelOld.uuid == modelNew.uuid
                            && modelOld.name == modelNew.name
                            && modelOld.path == modelNew.path
                }
                is File ->{
                    val modelOld : File = oldList[oldItemPosition] as File
                    val modelNew : File  = newList[newItemPosition] as File
                    return  modelOld.name == modelNew.name
                            && modelOld.path == modelNew.path
                }
            }
        return false
    }
}