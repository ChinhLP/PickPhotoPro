package com.apero.pickphoto.api.config.action

import android.app.Activity
import java.lang.ref.WeakReference

interface VslPickPhotoActionConfig {
    fun actionAfterApprove(
        pathImage: String,
        weakActivity: WeakReference<Activity>
    )

    fun actionBack(
        weakActivity: WeakReference<Activity>
    )
}
