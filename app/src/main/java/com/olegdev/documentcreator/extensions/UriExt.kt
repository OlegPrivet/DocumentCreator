package com.olegdev.documentcreator.extensions

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.olegdev.documentcreator.BuildConfig
import java.io.File


/**Created by Oleg
 * @Date: 24.10.2021
 * @Email: karandalli35@gmail.com
 **/

fun Uri.contentUriFromFile(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".fileprovider",
            File(this.path.toString())
        )
    } else {
        Uri.fromFile(File(this.path.toString()))
    }
}