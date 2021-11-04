package com.olegdev.documentcreator.repositories

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.olegdev.documentcreator.database.FileDatabase
import com.olegdev.documentcreator.extensions.fileToDocument
import com.olegdev.documentcreator.models.Document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

private const val DATABASE_NAME = "file-database"

class FileRepository private constructor(context: Context) {

    private val database: FileDatabase = Room.databaseBuilder(
        context.applicationContext,
        FileDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val fileDao = database.fileDao()

    fun getFiles(): LiveData<List<Document>> = fileDao.getFiles()
    suspend fun getFilesNoLiveData(): List<Document> = fileDao.getFilesNoLiveData()
    fun getFile(uuid: UUID): LiveData<Document> = fileDao.getFile(uuid = uuid)
    fun getFileOnName(name: String): Document = fileDao.getFileOnName(name = name)

    suspend fun updateDocument(document: Document) {
        fileDao.updateFile(document)
    }

    suspend fun addDocument(document: Document) {
        fileDao.addFile(document)
    }

    suspend fun deleteDocument(document: Document) {
        fileDao.deleteFile(document)
    }

    fun searchDatabase(searchQuery: String): Flow<List<Document>> {
        return fileDao.searchDatabase(searchQuery)
    }

    fun filesFromDevice(path: String): MutableLiveData<MutableList<File>> {
        val results = MutableLiveData<MutableList<File>>()
        val mainDirectory = File(path)
        val arrayFiles: MutableList<File> = ArrayList()
        val files = mainDirectory.listFiles()
        files?.forEach {
            if (it.isDirectory && !it.isHidden)
                arrayFiles.add(it)
        }
        files?.forEach {
            if (it.name.lowercase().endsWith(".pdf"))
                arrayFiles.add(it)
        }
        results.postValue(arrayFiles)
        return results
    }

    fun documentFromExplorer(file: File): UUID {
        var document = Document()
        CoroutineScope(Dispatchers.IO).launch {
            document = file.fileToDocument()
            val doc = getFileOnName(document.name)
            if (doc == null || doc.name != document.name) {
                addDocument(document)
            } else {
                document = doc
            }
        }
        return document.uuid
    }

    fun documentFromDevice(context: Context, uri: Uri) : UUID{
        var document = Document()
        val file = DocumentFile.fromSingleUri(context, uri)
        CoroutineScope(Dispatchers.IO).launch {
            file?.let {
                document.name = file.name.toString()
                document.path = uri
                document.cachepath = Uri.parse(handleUri(context, uri))
                document.size = file.length().toString()
                val doc = getFileOnName(document.name)
                if (doc == null || doc.name != document.name) {
                    addDocument(document)
                } else {
                    document = doc
                }
            }
        }
        return document.uuid
    }


    private fun handleUri(context: Context, uri: Uri): String? {
        context.apply {
            val dir = File(cacheDir, "files").apply { mkdir() }
            val file = File(dir, "${uri.lastPathSegment}.pdf")
            contentResolver.openInputStream(uri)?.let {
                copyStreamToFile(it, file)
            }
            return file.path
        }

    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    companion object {
        private var INSTANCE: FileRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = FileRepository(context = context)
            }
        }

        fun get(): FileRepository {
            return INSTANCE ?: throw IllegalStateException("FileRepository must be initialized")
        }
    }

}