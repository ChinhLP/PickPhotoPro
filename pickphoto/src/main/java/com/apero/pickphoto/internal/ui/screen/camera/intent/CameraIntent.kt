package com.apero.pickphoto.internal.ui.screen.camera.intent

import android.net.Uri

sealed class CameraIntent {
    data class SetUriImageCapture(val uri: Uri?) : CameraIntent()
}