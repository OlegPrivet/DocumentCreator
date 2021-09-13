package com.olegdev.documentcreator.models

import android.net.Uri
import kotlinx.parcelize.Parcelize

@Parcelize
class Document @JvmOverloads constructor(override var id: Long = 0,
                                         override var name: String,
                                         override var path: Uri,
                                         var mimeType: String? = null,
                                         var size: String? = null,
                                         var fileType: FileType? = null,
                                         var date_modified: Long? = null
) : BaseFile(id, name, path)