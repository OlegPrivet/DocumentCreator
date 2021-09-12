package com.olegdev.documentcreator.models.sorting

import com.olegdev.documentcreator.models.Document
import java.util.Comparator

enum class SortingTypes(val comparator: Comparator<Document>?) {
    NAME(NameComparator()), NONE(null);
}