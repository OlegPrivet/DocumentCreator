package com.olegdev.documentcreator.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
class FileType constructor(
    var title: String,
    var extensions : Array<String>,
    @DrawableRes
    var drawable: Int
) : Parcelable