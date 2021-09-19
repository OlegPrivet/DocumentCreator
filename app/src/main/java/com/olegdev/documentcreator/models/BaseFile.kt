package com.olegdev.documentcreator.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class BaseFile(open var id: Int = 0,
                    open var name: String,
                    open var path: Uri
) : Parcelable
