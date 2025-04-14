package com.apero.pickphotopro.preview.config

import android.app.Activity
import android.content.Intent
import com.apero.pickphoto.api.config.action.VslPickPhotoActionConfig
import com.apero.pickphotopro.preview.preview.PreviewActivity
import com.apero.pickphotopro.preview.preview.PreviewActivity.Companion.IMAGE_PREVIEW
import java.lang.ref.WeakReference

class PickPhotoActionConfig() : VslPickPhotoActionConfig {
    override fun actionAfterApprove(pathImage: String?, weakActivity: WeakReference<Activity>) {
        weakActivity.get()?.let { activity ->
            activity.startActivity(
                Intent(activity, PreviewActivity::class.java).putExtra(
                    IMAGE_PREVIEW,
                    pathImage
                )
            )
            activity.finish()
        }
    }

    override fun actionBack(weakActivity: WeakReference<Activity>) {
        weakActivity.get()?.let { activity ->
            activity.finish()
        }
    }
}