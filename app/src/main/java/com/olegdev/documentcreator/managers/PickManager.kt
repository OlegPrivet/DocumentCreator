package com.olegdev.documentcreator.managers

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.repositories.FileRepository
import com.olegdev.documentcreator.viewmodels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PickManager(app: Application) : BaseViewModel(app) {

    private val TAG = PickManager::class.simpleName
    private val fileRepository = FileRepository.get()
    private lateinit var fileList: List<Document>

    fun searchDocs() {
        startDataLoad {
            fileList = fileRepository.getFilesNoLiveData()
            queryDocs()
        }
    }

    @WorkerThread
    suspend fun queryDocs(){
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
            val where = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")

            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getCursor(mediaStore = MediaStore.VOLUME_EXTERNAL_PRIMARY,
                    projection = DOC_PROJECTION,
                    mimeType = where,
                    fileType = pdf
                )
            } else {
                getCursor(mediaStore = MediaStore.VOLUME_EXTERNAL,
                    projection = DOC_PROJECTION,
                    mimeType = where,
                    fileType = pdf
                )
            }

            if (cursor != null) {
                getDocumentFromCursor(cursor)
                cursor.close()
            }
        }
    }

    @WorkerThread
    private suspend fun getDocumentFromCursor(data: Cursor){
        while (data.moveToNext()) {
            val fileId = data.getInt(data.getColumnIndexOrThrow(BaseColumns._ID))
            val path = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            val title =
                data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))

            if (path != null) {

                val file = File(path)
                val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentUris.withAppendedId(
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                        fileId.toLong()
                    )
                } else {
                    ContentUris.withAppendedId(
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                        fileId.toLong()
                    )
                }
                if (!file.isDirectory && file.exists()) {

                    var mimeType =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                    if (mimeType == null && TextUtils.isEmpty(mimeType)) {
                        mimeType = ""
                    }
                    val last_edit =
                        data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))

                    val size =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))

                    val document = Document(
                        id_file = fileId,
                        name = title,
                        path = contentUri,
                        mimeType = mimeType,
                        size = size,
                        date_modified = last_edit * 1000L,
                    )
                    if (fileList.isEmpty()){
                        fileRepository.addDocument(document)
                    }else{
                        val doc = fileRepository.getFileOnPath(document.path)
                        if (doc == null || doc.id_file != document.id_file){
                            fileRepository.addDocument(document)
                        }
                    }
                }
            }
        }
    }

    private fun getCursor(mediaStore: String, projection: Array<String>, mimeType: String, fileType: String?) : Cursor? {
        return getApplication<Application>().contentResolver.query(
            MediaStore.Files.getContentUri(mediaStore),
            projection,
            mimeType,
            arrayOf(fileType),
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        )
    }

}