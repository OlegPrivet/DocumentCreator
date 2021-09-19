package com.olegdev.documentcreator.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun dateFormat(date: Long, format: String) : String{
        return SimpleDateFormat(format, Locale.ENGLISH).format(Date(date)).toString()
    }
}