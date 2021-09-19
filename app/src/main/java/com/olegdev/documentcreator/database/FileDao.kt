package com.olegdev.documentcreator.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.olegdev.documentcreator.models.Document

@Dao
interface FileDao {
    @Query("SELECT * FROM document")
    fun getFiles():LiveData<List<Document>>
    @Query("SELECT * FROM document")
    suspend fun getFilesNoLiveData():List<Document>
    @Query("SELECT * FROM document WHERE id = (:id)")
    fun getFile(id: Int):LiveData<Document>
    @Update
    suspend fun updateFile(document: Document)
    @Insert
    suspend fun addFile(document: Document)
    @Delete
    suspend fun deleteFile(document: Document)
}