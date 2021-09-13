package com.olegdev.documentcreator.viewmodels

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.olegdev.documentcreator.managers.PickManager
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.models.FileType
import com.olegdev.documentcreator.utils.FilePickUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class DocViewModel(app : Application) : BaseViewModelPicData(app) {

    private val _lvDocData = MutableLiveData<ArrayList<Document>>()
    val lvDocData: LiveData<ArrayList<Document>>
        get() = _lvDocData


    fun getDocs(fileTypes: ArrayList<FileType>, comparator: Comparator<Document>?) {
        startDataLoad {
            val dirs = queryDocs(fileTypes, comparator)
            _lvDocData.postValue(dirs)
        }
    }

    @WorkerThread
    suspend fun queryDocs(
        fileTypes: ArrayList<FileType>,
        comparator: Comparator<Document>?
    ): ArrayList<Document> {
        var data = ArrayList<Document>()
        withContext(Dispatchers.IO) {

            val selection =
                ("${MediaStore.Files.FileColumns.MEDIA_TYPE}!=${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}" +
                        " AND ${MediaStore.Files.FileColumns.MEDIA_TYPE}!=${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}")

            val DOC_PROJECTION = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.TITLE
            )

            val cursor = getApplication<Application>().contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                DOC_PROJECTION,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
            )

            if (cursor != null) {
                data = createDocumentType(fileTypes, comparator, getDocumentFromCursor(cursor))
                cursor.close()
            }
        }
        return data
    }

    @WorkerThread
    private fun createDocumentType(
        fileTypes: ArrayList<FileType>,
        comparator: Comparator<Document>?,
        documents: MutableList<Document>
    ): ArrayList<Document> {
        var documentMap = ArrayList<Document>()

        for (fileType in fileTypes) {
            val documentListFilteredByType = documents.filter { document ->
                FilePickUtils.contains(
                    fileType.extensions,
                    document.mimeType
                )
            }

            comparator?.let {
                documentListFilteredByType.sortedWith(comparator)
            }

            documentMap = documentListFilteredByType as ArrayList<Document>
        }

        return documentMap
    }

    @WorkerThread
    private fun getDocumentFromCursor(data: Cursor): MutableList<Document> {
        val documents = mutableListOf<Document>()
        while (data.moveToNext()) {

            val imageId = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID))
            val path = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            val title =
                data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))

            if (path != null) {

                val fileType = getFileType(PickManager.getFileTypes(), path)
                val file = File(path)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                    imageId
                )
                if (fileType != null && !file.isDirectory && file.exists()) {

                    val document = Document(imageId, title, contentUri)
                    document.fileType = fileType

                    val mimeType =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                    if (mimeType != null && !TextUtils.isEmpty(mimeType)) {
                        document.mimeType = mimeType
                    } else {
                        document.mimeType = ""
                    }
                    val last_edit =
                        data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))
                    document.date_modified = last_edit

                    document.size =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))

                    if (!documents.contains(document)) documents.add(document)
                }
            }
        }

        return documents
    }

    private fun getFileType(types: ArrayList<FileType>, path: String): FileType? {
        for (index in types.indices) {
            for (string in types[index].extensions) {
                if (path.endsWith(string)) return types[index]
            }
        }
        return null
    }

}