package com.olegdev.documentcreator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.olegdev.documentcreator.extension.setupWithNavController

//https://github.com/dmytrodanylyk/android-morphing-button
//https://github.com/zhihu/Matisse-selected files
//https://github.com/kashifo/android-folder-picker-library
//https://github.com/ArthurHub/Android-Image-Cropper
//https://github.com/airbnb/lottie-android - AdobeAfterEffects animation
//https://github.com/burhanrashid52/PhotoEditor
//https://github.com/barteksc/AndroidPdfViewer

class MainActivity : AppCompatActivity() {

    private lateinit var bottomView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        bottomView = findViewById(R.id.bottom_view)

        bottomView.setupWithNavController(
            navGraphIds = listOf(
                R.navigation.home_nav_graph,
                R.navigation.settings_nav_graph
            ),
            fragmentManager = supportFragmentManager,
            containerId = R.id.navController,
            intent = intent
        )
    }
}