package com.apero.pickphotopro.api

import android.app.Activity
import java.lang.ref.WeakReference

interface VslModulePickPhotoApi {
    fun openSdkPickPhoto(weakActivity: WeakReference<Activity>)
}