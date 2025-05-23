package com.apero.pickphoto.internal.ui.screen.camera.intent

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CameraState(
    val uriImage: Uri? = null
) : Parcelable