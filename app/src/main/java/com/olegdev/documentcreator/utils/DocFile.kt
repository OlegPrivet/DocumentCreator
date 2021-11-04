package com.olegdev.documentcreator.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.olegdev.documentcreator.extensions.contentUriFromFile


/**Created by Oleg
 * @Date: 24.10.2021
 * @Email: karandalli35@gmail.com
 **/
object DocFile{
    private val TAG = DocFile::class.simpleName
    fun fromCustomUri(context: Context, uri: Uri): DocumentFile? {
        return when {
            uri.scheme.equals("file") -> {
                DocumentFile.fromSingleUri(context, uri.contentUriFromFile(context))
            }
            uri.scheme.equals("content") -> {
                DocumentFile.fromSingleUri(context, uri)
            }
            else -> {
                DocumentFile.fromSingleUri(context, uri.contentUriFromFile(context))
            }
        }
    }
}


