package com.apero.pickphoto.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
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
        onCancelRequest: () -> Unit = {}
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
                onCancelRequest.invoke()
            }
        }
    }

    fun hasFullPhotoPermission(weakActivity: WeakReference<Activity>): Boolean {
        val activity = weakActivity.get() ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 6 - 12
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}