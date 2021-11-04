package com.olegdev.documentcreator.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.olegdev.documentcreator.repositories.FileRepository
import java.io.File
import java.util.*

/**Created by Oleg
 * @Date: 27.10.2021
 * @Email: karandalli35@gmail.com
 **/

class FileExplorerViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = FileExplorerViewModel::class.simpleName
    private val fileRepository = FileRepository.get()
    var currentPath: String = System.getenv("EXTERNAL_STORAGE")
    var arrPath = currentPath.split("/") as ArrayList
    private val context by lazy { getApplication<Application>().applicationContext }
    private val _files = MutableLiveData<String>()
    val files: LiveData<MutableList<File>> =
        Transformations.switchMap(_files) { path ->
            fileRepository.filesFromDevice(path)
        }

    fun getFiles(path: String){
        arrPath = path.split("/") as ArrayList<String>
        arrPath.removeAt(0)
        _files.postValue(path)
    }

    fun addFile(file: File) : UUID{
        return fileRepository.documentFromExplorer(file)
    }

}