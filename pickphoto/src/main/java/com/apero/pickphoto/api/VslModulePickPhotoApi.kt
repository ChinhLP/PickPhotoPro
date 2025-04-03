package com.apero.pickphoto.api

import android.app.Activity
import java.lang.ref.WeakReference

interface VslModulePickPhotoApi {
    fun openSdkPickPhoto(weakActivity: WeakReference<Activity>)
}