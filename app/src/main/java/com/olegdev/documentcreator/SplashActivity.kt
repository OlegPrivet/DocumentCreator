package com.olegdev.documentcreator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.olegdev.documentcreator.constants.SharedPrefConstant.AUTO_SPACING
import com.olegdev.documentcreator.constants.SharedPrefConstant.NIGHT_MODE
import com.olegdev.documentcreator.constants.SharedPrefConstant.PAGE_FLING
import com.pixplicity.easyprefs.library.Prefs

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Prefs.contains(AUTO_SPACING)){
            Prefs.putBoolean(AUTO_SPACING, true)
            Prefs.putBoolean(PAGE_FLING, true)
            Prefs.putBoolean(NIGHT_MODE, resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

    }
}