package com.apero.pickphoto.internal.ui.screen.pickphoto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.base.BaseComposeActivity
import com.apero.pickphoto.internal.designsystem.pxToDp
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoIntent
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoState
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoViewModel
import com.apero.pickphoto.internal.ui.screen.pickphoto.widget.PickPhotoItem
import java.lang.ref.WeakReference

internal class PickPhotoActivity : BaseComposeActivity() {

    private val viewModel by lazy {
        DIContainer.viewModelContainer.getViewModel(this, PickPhotoViewModel::class.java)
    }

    override fun onBack() {
        DIContainer.vslPickPhotoActionConfig.actionBack(WeakReference(this))
    }

    @Composable
    override fun SetupUi() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) {
            viewModel.onEvent(PickPhotoIntent.LoadInitPhotos(this@PickPhotoActivity))
            viewModel.onEvent(PickPhotoIntent.LoadFolders(this@PickPhotoActivity))
        }
        PickPhotoScreen(
            uiState = uiState,
            onLoadMorePickPhotos = {
                viewModel.onEvent(
                    PickPhotoIntent.LoadMorePhotos(
                        this@PickPhotoActivity,
                        uiState.lastItemPickPhoto.dateAdded
                    )
                )
            },
            onLoadMorePhotoInFolder = {
                viewModel.onEvent(
                    PickPhotoIntent.LoadMoreImageInFolder(
                        this@PickPhotoActivity,
                        uiState.folderSelected.folderId,
                        50,
                        uiState.lastItemInFolder.dateAdded
                    )
                )
            },
            onPhotoSelected = {
                viewModel.onEvent(PickPhotoIntent.SelectPhoto(uiState.itemSelected))
            })
    }
}


@Composable
internal fun PickPhotoScreen(
    uiState: PickPhotoState,
    onLoadMorePickPhotos: () -> Unit,
    onLoadMorePhotoInFolder: () -> Unit,
    onPhotoSelected: (String) -> Unit,
) {
    Scaffold(
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(20.pxToDp()),
                    horizontalArrangement = Arrangement.spacedBy(10.pxToDp()),
                    verticalArrangement = Arrangement.spacedBy(12.pxToDp()),
                    columns = GridCells.Fixed(3)
                ) {
                    item {
                        PickPhotoItem(modifier = Modifier.size(100.pxToDp())) {}
                    }
                    item {
                        PickPhotoItem(modifier = Modifier.size(100.pxToDp())) {}
                    }
                    item {
                        PickPhotoItem(modifier = Modifier.size(100.pxToDp())) {}
                    }
                    item {
                        PickPhotoItem(modifier = Modifier.size(100.pxToDp())) {}
                    }
                    item {
                        PickPhotoItem(modifier = Modifier.size(100.pxToDp())) {}
                    }
                }
            }

        }
    )
}

@Preview
@Composable
internal fun PreviewPickPhotoScreen() {
    PickPhotoScreen(uiState = PickPhotoState(), {}, {}, {})
}