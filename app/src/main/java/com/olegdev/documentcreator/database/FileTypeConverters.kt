package com.olegdev.documentcreator.database

import android.net.Uri
import androidx.room.TypeConverter

class FileTypeConverters {
    @TypeConverter
    fun fromUri(uri: Uri):String{
        return uri.toString()
    }
    @TypeConverter
    fun toUri(path: String):Uri{
        return Uri.parse(path)
    }
}