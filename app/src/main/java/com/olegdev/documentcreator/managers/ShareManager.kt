package com.olegdev.documentcreator.managers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import com.olegdev.documentcreator.R
import com.olegdev.documentcreator.states.FileState
import com.olegdev.documentcreator.utils.DocFile




/**Created by Oleg
 * @Date: 24.10.2021
 * @Email: karandalli35@gmail.com
 **/
class ShareManager(val context: Context) {

    fun shareDoc(uri: Uri) : FileState {
        var result: FileState = FileState.DefaultState()
        val file = DocFile.fromCustomUri(context = context, uri = uri)
        file?.let {
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "application/pdf"
            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("${it.uri}"))
            intentShare.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intentShare.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intentShare.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            intentShare.putExtra(
                Intent.EXTRA_SUBJECT,
                "${context.resources.getString(R.string.send_from)} ${context.resources.getString(R.string.app_name)}"
            )
            val resInfoList: List<ResolveInfo> = context.packageManager
                .queryIntentActivities(intentShare, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            context.startActivity(
                Intent.createChooser(
                    intentShare,
                    context.resources.getString(R.string.send_file)
                )
            )
        } ?: run {
            result = FileState.FileStateError(R.string.file_not_found)
        }
        return result
    }
}