package com.apero.pickphoto.internal.ui.screen.pickphoto

import androidx.compose.runtime.Composable
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.base.BaseComposeActivity
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoViewModel

internal class PickPhotoActivity : BaseComposeActivity() {

    private val pickPhotoViewModel by lazy {
        DIContainer.viewModelContainer.getViewModel(this, PickPhotoViewModel::class.java)
    }

    override fun onBack() {
        TODO("Not yet implemented")
    }

    @Composable
    override fun SetupUi() {
        TODO("Not yet implemented")
    }
}