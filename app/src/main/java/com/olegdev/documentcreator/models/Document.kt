package com.olegdev.documentcreator.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class Document(
    @PrimaryKey val uuid: UUID = UUID.randomUUID(),
    var id_file: Int = 0,
    var name: String = "",
    var path: Uri = Uri.parse(""),
    var cachepath: Uri = Uri.parse(""),
    var size: String? = null,
    var date_modified: Long? = System.currentTimeMillis(),
    var currentPage: Int = 0
) : Parcelable