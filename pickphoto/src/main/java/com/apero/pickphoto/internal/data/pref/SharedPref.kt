package com.apero.pickphoto.internal.data.pref

import android.content.Context
import com.apero.pickphoto.di.DIContainer.application
import com.apero.pickphoto.internal.data.pref.PrefConst.IS_FIRST_REQUEST_PERMISSION_CAMERA
import androidx.core.content.edit

internal class SharedPref {
    private val sharedPrefName = application.packageName + ".pickphoto" + "_preferences"

    private val sharedPref =
        application.applicationContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

    var isFirstRequestPermissionCamera: Boolean
        get() = sharedPref.getBoolean(IS_FIRST_REQUEST_PERMISSION_CAMERA, true)
        set(value) = sharedPref.edit { putBoolean(IS_FIRST_REQUEST_PERMISSION_CAMERA, value) }

    var isFirstRequestPermissionGallery: Boolean
        get() = sharedPref.getBoolean(PrefConst.IS_FIRST_REQUEST_PERMISSION_GALLERY, true)
        set(value) = sharedPref.edit {
            putBoolean(
                PrefConst.IS_FIRST_REQUEST_PERMISSION_GALLERY,
                value
            )
        }

}