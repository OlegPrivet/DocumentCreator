package com.olegdev.documentcreator.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.repositories.FileRepository
import kotlinx.coroutines.launch

class FileListViewModel: ViewModel() {

    private val fileRepository = FileRepository.get()
    val fileListLiveData = fileRepository.getFiles()

    fun addFiles(document: Document) = viewModelScope.launch {
        fileRepository.addDocument(document)
    }
}