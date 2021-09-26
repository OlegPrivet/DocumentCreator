package com.olegdev.documentcreator.app

import android.app.Application
import android.content.ContextWrapper
import com.olegdev.documentcreator.repositories.FileRepository
import com.pixplicity.easyprefs.library.Prefs

class DocumentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FileRepository.initialize(context = this)
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}