package com.olegdev.documentcreator.managers

import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.constants.FilePickConstant
import com.olegdev.documentcreator.models.FileType
import com.olegdev.documentcreator.models.sorting.SortingTypes
import java.util.*
import kotlin.collections.ArrayList

object PickManager {

    var sortingType = SortingTypes.NAME

    private val fileTypes: LinkedHashSet<FileType> = LinkedHashSet()

    private fun addDocTypes() {
        val pdfs = arrayOf("pdf")
        fileTypes.add(FileType(FilePickConstant.PDF, pdfs, R.drawable.ic_pdf))

        /*val docs = arrayOf("doc", "docx", "dot", "dotx")
        fileTypes.add(FileType(FilePickConstant.DOC, docs, R.drawable.ic_doc))

        val ppts = arrayOf("ppt", "pptx")
        fileTypes.add(FileType(FilePickConstant.PPT, ppts, R.drawable.ic_ppt))

        val xlss = arrayOf("xls", "xlsx")
        fileTypes.add(FileType(FilePickConstant.XLS, xlss, R.drawable.ic_xls))

        val txts = arrayOf("txt")
        fileTypes.add(FileType(FilePickConstant.TXT, txts, R.drawable.ic_txt))*/
    }

    fun getFileTypes(): java.util.ArrayList<FileType> {
        addDocTypes()
        return ArrayList(fileTypes)
    }

}