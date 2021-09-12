package com.olegdev.documentcreator.utils

import android.webkit.MimeTypeMap
import java.io.File

object FilePickUtils {

    fun getFileExtension(file: File): String {
        val name = file.name
        return try {
            name.substring(name.lastIndexOf(".") + 1)
        } catch (e: Exception) {
            ""
        }

    }

    fun contains(types: Array<String>, mimeType: String?): Boolean {
        for (type in types) {
            if(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type) == mimeType){
                return true
            }
        }
        return false
    }

}