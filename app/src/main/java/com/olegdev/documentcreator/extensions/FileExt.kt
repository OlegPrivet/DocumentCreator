package com.olegdev.documentcreator.extensions

import android.net.Uri
import com.olegdev.documentcreator.models.Document
import java.io.File


/**Created by Oleg
 * @Date: 31.10.2021
 * @Email: karandalli35@gmail.com
 **/

fun File.fileToDocument() : Document {
    return Document(
        name = this.name,
        path = Uri.fromFile(this),
        cachepath = Uri.fromFile(this),
        size = this.length().toString(),
        date_modified = this.lastModified()
    )
}