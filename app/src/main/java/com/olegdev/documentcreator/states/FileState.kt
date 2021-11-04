package com.olegdev.documentcreator.states

import com.olegdev.documentcreator.models.Document
import java.util.*

sealed class FileState{
    class DefaultState : FileState()
    class FileStateUpload : FileState()
    class FileStateDownload(val uuid: UUID) : FileState()
    class FileStateError(val message: Int) : FileState()
    class FileStateMoreDialog(val document: Document) : FileState()
}
