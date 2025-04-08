package com.apero.pickphoto.internal.ui.screen.pickphoto

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.base.BaseComposeActivity
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoViewModel
import java.lang.ref.WeakReference

internal class PickPhotoActivity : BaseComposeActivity() {

    private val pickPhotoViewModel by lazy {
        DIContainer.viewModelContainer.getViewModel(this, PickPhotoViewModel::class.java)
    }

    override fun onBack() {
        DIContainer.vslPickPhotoActionConfig.actionBack(WeakReference(this))
    }

    @Composable
    override fun SetupUi() {
        PickPhotoScreen()
    }
}


@Composable
fun PickPhotoScreen() {
    Scaffold(
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    
                }
            }

        }
    )
}

@Preview
@Composable
fun PreviewPickPhotoScreen() {
    PickPhotoScreen()
}