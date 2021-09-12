package com.olegdev.documentcreator.models.sorting

import com.olegdev.documentcreator.models.Document
import java.util.*

class NameComparator : Comparator<Document> {
    override fun compare(o1: Document, o2: Document): Int {
        return o1.name.lowercase(Locale.getDefault()).compareTo(o2.name.lowercase(Locale.getDefault()))
    }
}
