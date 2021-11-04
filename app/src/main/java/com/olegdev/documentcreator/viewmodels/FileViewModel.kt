package com.olegdev.documentcreator.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.olegdev.documentcreator.models.Document
import com.olegdev.documentcreator.repositories.FileRepository
import kotlinx.coroutines.launch
import java.util.*




class FileViewModel(application: Application) : AndroidViewModel(application) {

    private val fileRepository = FileRepository.get()
    private val fileIdLiveData = MutableLiveData<UUID>()
    var fileLiveData: LiveData<Document> =
        Transformations.switchMap(fileIdLiveData) { docId ->
            fileRepository.getFile(docId)
        }
    private val context by lazy { getApplication<Application>().applicationContext }

    private val _barsShown = MutableLiveData<Boolean>()
    val barsShown: LiveData<Boolean>
        get() = _barsShown


    fun loadFile(docId: UUID){
        fileIdLiveData.value = docId
    }

    fun setBarsShown(show: Boolean){
        _barsShown.postValue(show)
    }

    fun saveFile(document: Document) = viewModelScope.launch {
        fileRepository.updateDocument(document)
    }

}