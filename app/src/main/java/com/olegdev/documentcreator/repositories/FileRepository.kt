package com.olegdev.documentcreator.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.olegdev.documentcreator.database.FileDatabase
import com.olegdev.documentcreator.models.Document

private const val DATABASE_NAME = "file-database"

class FileRepository private constructor(context: Context){

    private val database: FileDatabase = Room.databaseBuilder(
        context.applicationContext,
        FileDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val fileDao = database.fileDao()

    fun getFiles(): LiveData<List<Document>> = fileDao.getFiles()
    suspend fun getFilesNoLiveData(): List<Document> = fileDao.getFilesNoLiveData()
    fun getFile(id: Int): LiveData<Document> = fileDao.getFile(id = id)

    suspend fun updateDocument(document: Document) {
        fileDao.updateFile(document)
    }

    suspend fun addDocument(document: Document) {
        fileDao.addFile(document)
    }

    suspend fun deleteDocument(document: Document) {
        fileDao.deleteFile(document)
    }

    companion object{
        private var INSTANCE: FileRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = FileRepository(context = context)
            }
        }

        fun get():FileRepository{
            return INSTANCE ?: throw IllegalStateException("FileRepository must be initialized")
        }
    }

}