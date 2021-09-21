package com.olegdev.documentcreator.database

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

class FileTypeConverters {
    @TypeConverter
    fun fromUri(uri: Uri):String{
        return uri.toString()
    }
    @TypeConverter
    fun toUri(path: String):Uri{
        return Uri.parse(path)
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?):String? {
        return uuid?.toString()
    }
}