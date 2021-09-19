package com.olegdev.documentcreator.models.sorting

import com.olegdev.documentcreator.models.Document
import java.util.*

enum class SortingTypes(val comparator: Comparator<Document>?) {
    NAME(NameComparator()),
    NONE(null);
}