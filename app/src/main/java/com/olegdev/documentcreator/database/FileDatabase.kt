package com.olegdev.documentcreator.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.olegdev.documentcreator.models.Document

@Database(entities = [Document::class], version = 1)
@TypeConverters(FileTypeConverters::class)
abstract class FileDatabase : RoomDatabase(){
    abstract fun fileDao(): FileDao
}