package com.olegdev.documentcreator.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.olegdev.documentcreator.R


object PermUtils {

    val PERMISSIONS_STORAGE = mutableListOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun hasPermissions(context: Context): Boolean = PERMISSIONS_STORAGE.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPerms(
        activity: Activity,
        perms: MutableCollection<String>,
        fab: FloatingActionButton?
    ): Boolean {
        var permsSuccess = false
        Dexter.withContext(activity.applicationContext)
            .withPermissions(perms)
            .withListener(
                CompositeMultiplePermissionsListener(
                    object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            permsSuccess = true
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            p1!!.continuePermissionRequest()
                        }
                    }, SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                        .with(activity.findViewById(R.id.main_view), R.string.perm_snack)
                        .withOpenSettingsButton(R.string.setting_btn)
                        .withCallback(object : Snackbar.Callback() {
                            override fun onShown(sb: Snackbar?) {
                                fab?.let {
                                    sb!!.anchorView = it
                                }
                                super.onShown(sb)
                            }

                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                            }
                        }).build()
                )
            ).check()
        return permsSuccess
    }

}