package com.olegdev.documentcreator.viewmodels

import androidx.lifecycle.*
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.repositories.FileRepository
import kotlinx.coroutines.launch
import java.util.*

class FileViewModel : ViewModel() {

    private val fileRepository = FileRepository.get()
    private val fileIdLiveData = MutableLiveData<UUID>()

    var fileLiveData: LiveData<Document> =
        Transformations.switchMap(fileIdLiveData) { docId ->
            fileRepository.getFile(docId)
        }

    fun loadFile(docId: UUID){
        fileIdLiveData.value = docId
    }

    fun saveFile(document: Document) = viewModelScope.launch {
        fileRepository.updateDocument(document)
    }
}