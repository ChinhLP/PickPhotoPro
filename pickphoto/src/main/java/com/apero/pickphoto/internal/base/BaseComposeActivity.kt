package com.apero.pickphoto.internal.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.designsystem.AppTheme
import com.apero.pickphoto.util.ext.hideSystemBar
import java.util.Locale

internal abstract class BaseComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()
        setContent {
            AppTheme {
                SetupUi()
            }
        }
        onBackPressedDispatcher.addCallback {
            onBack()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            super.attachBaseContext(
                updateResources(
                    newBase,
                    DIContainer.vslPickPhotoModuleContainer.language
                )
            )
        } else {
            super.attachBaseContext(newBase)
        }
    }

    private fun updateResources(
        context: Context, language: String,
    ): Context {
        val locale: Locale
        if (language.contains("-")) {
            val splitLanguage = language.split("-")
            locale = Locale(splitLanguage[0], splitLanguage[1])
        } else {
            locale = Locale(language)
        }
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    protected abstract fun onBack()

    @Composable
    protected abstract fun SetupUi()
}