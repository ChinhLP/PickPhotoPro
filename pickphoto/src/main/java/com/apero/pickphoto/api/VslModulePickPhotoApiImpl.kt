package com.apero.pickphoto.api

import android.app.Activity
import android.content.Intent
import com.apero.pickphoto.internal.ui.screen.pickphoto.PickPhotoActivity
import java.lang.ref.WeakReference

class VslModulePickPhotoApiImpl : VslModulePickPhotoApi {
    override fun openSdkPickPhoto(weakActivity: WeakReference<Activity>) {
        weakActivity.get()?.let { activity ->
            activity.startActivity(Intent(activity, PickPhotoActivity::class.java))
        }
    }
}