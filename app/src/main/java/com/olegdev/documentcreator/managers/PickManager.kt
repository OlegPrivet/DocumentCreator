package com.olegdev.documentcreator.managers

import android.content.pm.ActivityInfo
import android.net.Uri
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.constants.FilePickConstant
import com.olegdev.documentcreator.models.BaseFile
import com.olegdev.documentcreator.models.FileType
import com.olegdev.documentcreator.models.sorting.SortingTypes
import java.util.*
import kotlin.collections.ArrayList

object PickManager {

    private var maxCount = FilePickConstant.DEFAULT_MAX_COUNT
    private var showImages = true
    var sortingType = SortingTypes.NONE

    val selectedPhotos: java.util.ArrayList<Uri> = java.util.ArrayList()
    val selectedFiles: java.util.ArrayList<Uri> = java.util.ArrayList()

    private val fileTypes: LinkedHashSet<FileType> = LinkedHashSet()

    var title: String? = null

    private var showVideos: Boolean = false

    var isShowGif: Boolean = false

    private var showSelectAll = false

    var imageFileSize: Int = FilePickConstant.DEFAULT_FILE_SIZE
    var videoFileSize: Int = FilePickConstant.DEFAULT_FILE_SIZE

    var isDocSupport = true
        get() = field

    var isEnableCamera = true

    /**
     * Recyclerview span count for both folder and detail screen
     * Default Folder span is 2
     * Default Detail Span is 3
     */
    var spanTypes = mutableMapOf(
        FilePickConstant.SPAN_TYPE.FOLDER_SPAN to 2,
        FilePickConstant.SPAN_TYPE.DETAIL_SPAN to 3
    )

    /**
     * The preferred screen orientation this activity would like to run in.
     * From the {@link android.R.attr#screenOrientation} attribute, one of
     * {@link #SCREEN_ORIENTATION_UNSPECIFIED},
     * {@link #SCREEN_ORIENTATION_LANDSCAPE},
     * {@link #SCREEN_ORIENTATION_PORTRAIT},
     * {@link #SCREEN_ORIENTATION_USER},
     * {@link #SCREEN_ORIENTATION_BEHIND},
     * {@link #SCREEN_ORIENTATION_SENSOR},
     * {@link #SCREEN_ORIENTATION_NOSENSOR},
     * {@link #SCREEN_ORIENTATION_SENSOR_LANDSCAPE},
     * {@link #SCREEN_ORIENTATION_SENSOR_PORTRAIT},
     * {@link #SCREEN_ORIENTATION_REVERSE_LANDSCAPE},
     * {@link #SCREEN_ORIENTATION_REVERSE_PORTRAIT},
     * {@link #SCREEN_ORIENTATION_FULL_SENSOR},
     * {@link #SCREEN_ORIENTATION_USER_LANDSCAPE},
     * {@link #SCREEN_ORIENTATION_USER_PORTRAIT},
     * {@link #SCREEN_ORIENTATION_FULL_USER},
     * {@link #SCREEN_ORIENTATION_LOCKED},
     */
    var orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        get() = field

    var isShowFolderView = true

    val currentCount: Int
        get() = selectedPhotos.size + selectedFiles.size

    fun setMaxCount(count: Int) {
        reset()
        this.maxCount = count
    }

    fun getMaxCount(): Int {
        return maxCount
    }

    fun add(path: Uri?, type: Int) {
        if (path != null && shouldAdd()) {
            if (!selectedPhotos.contains(path) && type == FilePickConstant.FILE_TYPE_MEDIA) {
                selectedPhotos.add(path)
            } else if (!selectedFiles.contains(path) && type == FilePickConstant.FILE_TYPE_DOCUMENT) {
                selectedFiles.add(path)
            } else {
                return
            }
        }
    }

    fun add(paths: List<Uri>, type: Int) {
        for (index in paths.indices) {
            add(paths[index], type)
        }
    }

    fun remove(path: Uri?, type: Int) {
        if (type == FilePickConstant.FILE_TYPE_MEDIA && selectedPhotos.contains(path)) {
            selectedPhotos.remove(path)
        } else if (type == FilePickConstant.FILE_TYPE_DOCUMENT) {
            selectedFiles.remove(path)
        }
    }

    fun shouldAdd(): Boolean {
        return if (maxCount == -1) true else currentCount < maxCount
    }

    fun getSelectedFilePaths(files: java.util.ArrayList<BaseFile>): java.util.ArrayList<Uri> {
        val paths = java.util.ArrayList<Uri>()
        for (index in files.indices) {
            paths.add(files[index].path)
        }
        return paths
    }

    fun reset() {
        selectedFiles.clear()
        selectedPhotos.clear()
        fileTypes.clear()
        maxCount = -1
    }

    fun clearSelections() {
        selectedPhotos.clear()
        selectedFiles.clear()
    }

    fun deleteMedia(paths: List<Uri>) {
        selectedPhotos.removeAll(paths)
    }

    fun showVideo(): Boolean {
        return showVideos
    }

    fun setShowVideos(showVideos: Boolean) {
        this.showVideos = showVideos
    }

    fun showImages(): Boolean {
        return showImages
    }

    fun setShowImages(showImages: Boolean) {
        this.showImages = showImages
    }

    fun addFileType(fileType: FileType) {
        fileTypes.add(fileType)
    }

    fun addDocTypes() {
        val pdfs = arrayOf("pdf")
        fileTypes.add(FileType(FilePickConstant.PDF, pdfs, R.drawable.ic_pdf))

        val docs = arrayOf("doc", "docx", "dot", "dotx")
        fileTypes.add(FileType(FilePickConstant.DOC, docs, R.drawable.ic_doc))

        val ppts = arrayOf("ppt", "pptx")
        fileTypes.add(FileType(FilePickConstant.PPT, ppts, R.drawable.ic_ppt))

        val xlss = arrayOf("xls", "xlsx")
        fileTypes.add(FileType(FilePickConstant.XLS, xlss, R.drawable.ic_xls))

        val txts = arrayOf("txt")
        fileTypes.add(FileType(FilePickConstant.TXT, txts, R.drawable.ic_txt))
    }

    fun getFileTypes(): java.util.ArrayList<FileType> {
        return ArrayList(fileTypes)
    }

    fun hasSelectAll(): Boolean {
        return maxCount == -1 && showSelectAll
    }

    fun enableSelectAll(showSelectAll: Boolean) {
        this.showSelectAll = showSelectAll
    }

}