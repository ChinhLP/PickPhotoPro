package com.apero.pickphoto.internal.ui.screen.camera

import androidx.compose.runtime.Composable
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.base.BaseComposeActivity
import com.apero.pickphoto.internal.ui.screen.camera.intent.CameraViewModel

internal class CameraActivity : BaseComposeActivity() {
    private val cameraViewModel by lazy {
        DIContainer.viewModelContainer.getViewModel(this, CameraViewModel::class.java)
    }

    override fun onBack() {
        TODO("Not yet implemented")
    }

    @Composable
    override fun SetupUi() {
        TODO("Not yet implemented")
    }
}