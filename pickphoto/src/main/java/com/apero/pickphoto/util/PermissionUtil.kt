package com.apero.pickphoto.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.apero.pickphoto.internal.data.pref.SharedPref
import java.lang.ref.WeakReference

internal class PermissionUtil(val pref: SharedPref) {

    private fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }

            else -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun getCameraPermission(): String {
        return Manifest.permission.CAMERA
    }

    fun checkPermissionsPhoto(weakActivity: WeakReference<Activity>): Boolean {
        val activity = weakActivity.get() ?: return false
        val permissions = getRequiredPermissions()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermissionsPhoto(
        weakActivity: WeakReference<Activity>,
        requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
        onShowCustomDialog: (String) -> Unit = {}
    ) {
        val activity = weakActivity.get() ?: return
        val permissions = getRequiredPermissions()

        if (checkPermissionsPhoto(weakActivity).not()) {
            if (pref.isFirstRequestPermissionGallery || permissions.any {
                    shouldShowRequestPermissionRationale(
                        activity,
                        it
                    )
                }
            ) {
                pref.isFirstRequestPermissionGallery = false
                requestPermissionLauncher.launch(permissions)
            } else {
                onShowCustomDialog.invoke(TYPE_PERMISSION_GALLERY)
            }
        }
    }


    fun checkCameraPermission(weakActivity: WeakReference<Activity>): Boolean {
        val activity = weakActivity.get() ?: return false
        val permission = getCameraPermission()
        
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(
        weakActivity: WeakReference<Activity>,
        requestPermissionLauncher: ActivityResultLauncher<String>,
        onShowCustomDialog: (String) -> Unit = {}
    ) {
        val activity = weakActivity.get() ?: return
        val permission = getCameraPermission()

        if (checkCameraPermission(weakActivity).not()) {
            if (pref.isFirstRequestPermissionCamera || shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )
            ) {
                pref.isFirstRequestPermissionCamera = false
                requestPermissionLauncher.launch(permission)
            } else {
                onShowCustomDialog.invoke(TYPE_PERMISSION_CAMERA)
            }
        }
    }

    companion object {
        const val TYPE_PERMISSION_GALLERY = "TYPE_PERMISSION_GALLERY"
        const val TYPE_PERMISSION_CAMERA = "TYPE_PERMISSION_CAMERA"
    }
}