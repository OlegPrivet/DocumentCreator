package com.olegdev.documentcreator.managers

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.constants.FilePickConstant
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.models.FileType
import com.olegdev.documentcreator.repositories.FileRepository
import com.olegdev.documentcreator.utils.FilePickUtils
import com.olegdev.documentcreator.viewmodels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PickManager(app: Application) : BaseViewModel(app) {

    private val TAG = PickManager::class.simpleName

    /*private val _updateListFile = MutableLiveData<Boolean>()
    val updateListFile: LiveData<Boolean>
        get() = _updateListFile*/

    private val fileRepository = FileRepository.get()
    private lateinit var fileList : List<Document>

    fun searchDocs() {
        startDataLoad {
            fileList = fileRepository.getFilesNoLiveData()
            val update = queryDocs()
            //_updateListFile.postValue(update)
        }
    }

    @WorkerThread
    suspend fun queryDocs(): Boolean{
        var data = false
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

            val cursor = getApplication<Application>().contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                DOC_PROJECTION,
                where,
                arrayOf(pdf),
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
            )

            if (cursor != null) {
                data = getDocumentFromCursor(cursor)
                cursor.close()
            }
        }
        return data
    }

    @WorkerThread
    private fun createDocumentType(
        fileTypes: ArrayList<FileType>,
        comparator: Comparator<Document>?,
        documents: MutableList<Document>,
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
    private suspend fun getDocumentFromCursor(data: Cursor): Boolean {
        var update = false
        while (data.moveToNext()) {

            val fileId = data.getInt(data.getColumnIndexOrThrow(BaseColumns._ID))
            val path = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))

            Log.e(TAG, "fileId - $fileId")
            val title =
                data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))

            if (path != null) {

                val fileType = getFileType(getFileTypes(), path)
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

                    val document = Document(fileId, title, contentUri)

                    val mimeType =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                    if (mimeType != null && !TextUtils.isEmpty(mimeType)) {
                        document.mimeType = mimeType
                    } else {
                        document.mimeType = ""
                    }
                    val last_edit =
                        data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))
                    document.date_modified = last_edit * 1000L

                    document.size =
                        data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))

                    //Log.e(TAG, "fileList $fileList")
                    if(fileList.isNotEmpty()){
                        fileList.forEach {
                            if (it.id != document.id && it.name != document.name){
                                fileRepository.addDocument(document = document)
                                update = true
                            }
                        }
                    }else{
                        fileRepository.addDocument(document = document)
                        update = true
                    }
                }
            }
        }
        Log.e(TAG, "Update list $update")
        return update
    }

    private fun getFileType(types: ArrayList<FileType>, path: String): FileType? {
        for (index in types.indices) {
            for (string in types[index].extensions) {
                if (path.endsWith(string)) return types[index]
            }
        }
        return null
    }

    private val fileTypes: LinkedHashSet<FileType> = LinkedHashSet()

    private fun addDocTypes() {
        val pdfs = arrayOf("pdf")
        fileTypes.add(FileType(FilePickConstant.PDF, pdfs, R.drawable.ic_pdf))

        /*val docs = arrayOf("doc", "docx", "dot", "dotx")
        fileTypes.add(FileType(FilePickConstant.DOC, docs, R.drawable.ic_doc))

        val ppts = arrayOf("ppt", "pptx")
        fileTypes.add(FileType(FilePickConstant.PPT, ppts, R.drawable.ic_ppt))

        val xlss = arrayOf("xls", "xlsx")
        fileTypes.add(FileType(FilePickConstant.XLS, xlss, R.drawable.ic_xls))

        val txts = arrayOf("txt")
        fileTypes.add(FileType(FilePickConstant.TXT, txts, R.drawable.ic_txt))*/
    }

    fun getFileTypes(): java.util.ArrayList<FileType> {
        addDocTypes()
        return ArrayList(fileTypes)
    }

}