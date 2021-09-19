package com.olegdev.documentcreator.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
class Document @JvmOverloads constructor(@PrimaryKey override var id: Int = 0,
                                         override var name: String,
                                         override var path: Uri,
                                         var mimeType: String? = null,
                                         var size: String? = null,
                                         var date_modified: Long? = null,
                                         var currentPage: Int = 0
) : BaseFile(id, name, path)