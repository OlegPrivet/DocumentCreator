package com.olegdev.documentcreator.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.MediaColumns.*
import androidx.annotation.WorkerThread
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.extensions.default
import com.olegdev.documentcreator.extensions.set
import com.olegdev.documentcreator.managers.ShareManager
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.repositories.FileRepository
import com.olegdev.documentcreator.states.FileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class FileListViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = FileListViewModel::class.simpleName
    private val fileRepository = FileRepository.get()
    val fileListLiveData = fileRepository.getFiles()
    val state = MutableLiveData<FileState>()
        .default(initialValue = FileState.DefaultState())
    private lateinit var fileList: List<Document>
    private val context by lazy { getApplication<Application>().applicationContext }
    private val shareManager = ShareManager(context)

    fun addFile(uri: Uri){
        val uuid = fileRepository.documentFromDevice(context, uri)
        state.set(FileState.FileStateDownload(uuid))
    }

    fun addFiles(document: Document) = viewModelScope.launch {
        fileRepository.addDocument(document)
    }

    fun deleteDoc(document: Document) = viewModelScope.launch {
        fileRepository.deleteDocument(document)
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Document>> {
        return fileRepository.searchDatabase(searchQuery).asLiveData()
    }

    fun setState(newState: FileState) {
        state.set(newState)
    }

    fun moreDialog(document: Document){
        state.set(FileState.FileStateMoreDialog(document))
    }

    fun searchDoc(uri: Uri, isContract: Boolean = true) {
        state.set(FileState.FileStateUpload())
        CoroutineScope(Dispatchers.IO).launch {
            fileList = fileRepository.getFilesNoLiveData()
            queryDoc(uri, isContract)
        }
    }

    @WorkerThread
    suspend fun queryDoc(uri: Uri, isContract: Boolean) {
        val data = context.contentResolver.query(
            uri,
            FILE_PROJECTION,
            null,
            null,
            null
        )
        data?.use { it ->
            if (it.moveToFirst()) {
                var fileId = 0
                var title = "name"
                var last_edit = System.currentTimeMillis()
                var size = "0"
                if (it.columnNames.contains(_ID))
                    fileId = it.getInt(it.getColumnIndexOrThrow(_ID))
                if (it.columnNames.contains(DISPLAY_NAME))
                    title = it.getString(it.getColumnIndexOrThrow(DISPLAY_NAME))
                if (it.columnNames.contains(DATE_ADDED))
                    last_edit = it.getLong(it.getColumnIndexOrThrow(DATE_ADDED))
                if (it.columnNames.contains(SIZE))
                    size = it.getString(it.getColumnIndexOrThrow(SIZE))
                val pathUri: Uri = if (it.columnNames.contains(DATA)){
                    if (!isContract) {
                        DocumentFile.fromFile(File(it.getString(it.getColumnIndexOrThrow(DATA)))).uri
                    } else {
                        uri
                    }
                }else
                    uri

                var document = Document(
                    id_file = fileId,
                    name = title,
                    path = pathUri,
                    size = size,
                    date_modified = last_edit * 1000L,
                )
                if (fileList.isEmpty()) {
                    fileRepository.addDocument(document)
                } else {
                    val doc = fileRepository.getFileOnName(document.name)
                    if (doc == null || doc.name != document.name) {
                        fileRepository.addDocument(document)
                    } else {
                        document = doc
                    }
                }
                state.set(FileState.FileStateDownload(document.uuid))
            }
        }
        data?.close()
        fileList = emptyList()
    }

    private val FILE_PROJECTION = arrayOf(
        _ID,
        DISPLAY_NAME,
        DATA,
        SIZE,
        DATE_ADDED,
    )

    fun checkFiles() = viewModelScope.launch{
        fileList = fileRepository.getFilesNoLiveData()
        fileList.forEach { document ->
            val fileDoc = DocumentFile.fromSingleUri(context, document.path)
            /*if (!fileDoc!!.exists()){
                val file = File(document.cachepath.path.toString())
                if (file.exists())
                    file.delete()
                deleteDoc(document)
            }*/
        }
        fileList = emptyList()
    }

    fun moreFun(context: Context, document: Document){
        val items = arrayOf(
            context.resources.getString(R.string.delete),
            context.resources.getString(R.string.share)
        )
        MaterialAlertDialogBuilder(context)
            .setTitle(document.name)
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> {
                        deleteDoc(document)
                        dialog.dismiss()
                        state.set(FileState.DefaultState())
                    }
                    1 -> {
                        val result = shareManager.shareDoc(uri = document.path)
                        state.set(result)
                        dialog.dismiss()
                    }
                }
            }
            .setCancelable(true)
            .setOnCancelListener {
                state.set(FileState.DefaultState())
            }
            .show()
    }
}


