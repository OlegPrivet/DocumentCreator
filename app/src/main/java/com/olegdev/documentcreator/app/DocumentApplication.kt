package com.olegdev.documentcreator.app

import android.app.Application
import com.olegdev.documentcreator.repositories.FileRepository

class DocumentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FileRepository.initialize(context = this)
    }
}