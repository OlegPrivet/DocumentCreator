package com.olegdev.documentcreator.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.repositories.FileRepository

class FileViewModel(app: Application) : BaseViewModel(app) {

    private val fileRepository = FileRepository.get()
    private val fileIdLiveData = MutableLiveData<Int>()

    var fileLiveData: LiveData<Document> =
        Transformations.switchMap(fileIdLiveData) { docId ->
            fileRepository.getFile(docId)
        }

    fun loadFile(docId: Int){
        fileIdLiveData.value = docId
    }

    fun saveFile(document: Document) = startDataLoad {
        fileRepository.updateDocument(document)
    }
}