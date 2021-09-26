package com.olegdev.documentcreator.database

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.*
import com.olegdev.documentcreator.models.Document
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface FileDao {
    @Query("SELECT * FROM document")
    fun getFiles():LiveData<List<Document>>
    @Query("SELECT * FROM document")
    suspend fun getFilesNoLiveData():List<Document>
    @Query("SELECT * FROM document WHERE uuid = (:uuid)")
    fun getFile(uuid: UUID):LiveData<Document>
    @Query("SELECT * FROM document WHERE path = (:path)")
    fun getFileOnPath(path: Uri):Document
    @Update
    suspend fun updateFile(document: Document)
    @Insert
    suspend fun addFile(document: Document)
    @Delete
    suspend fun deleteFile(document: Document)
    @Query("SELECT * FROM document WHERE name LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<Document>>
}