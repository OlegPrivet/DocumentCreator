package com.olegdev.documentcreator.utils

object FilePickUtils {

    fun getBackPath(paths: ArrayList<String>): String {
        val sb = StringBuilder()
        paths.forEach {
            sb.append("/$it")
        }
        return sb.toString()
    }

}