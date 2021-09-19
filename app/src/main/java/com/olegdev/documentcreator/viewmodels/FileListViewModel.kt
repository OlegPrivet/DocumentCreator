package com.olegdev.documentcreator.viewmodels

import android.app.Application
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.repositories.FileRepository

class FileListViewModel(app: Application) : BaseViewModel(app) {

    private val fileRepository = FileRepository.get()
    val fileListLiveData = fileRepository.getFiles()

    fun addFiles(document: Document) = startDataLoad {
        fileRepository.addDocument(document)
    }
}