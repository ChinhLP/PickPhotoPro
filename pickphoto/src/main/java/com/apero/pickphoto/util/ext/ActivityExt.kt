package com.apero.pickphoto.util.ext

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

internal fun Activity.hideSystemBar(
    hideStatusBar: Boolean = false,
    hideNavigationBar: Boolean = true,
    isLightStatusBar: Boolean = true,
) {
    val windowInsetsController =
        WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    if (hideStatusBar) {
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
    }
    if (hideNavigationBar) {
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }
    windowInsetsController.isAppearanceLightStatusBars =
        isLightStatusBar

    window.statusBarColor = android.graphics.Color.WHITE
}