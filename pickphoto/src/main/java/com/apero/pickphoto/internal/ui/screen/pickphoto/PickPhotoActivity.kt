package com.apero.pickphoto.internal.ui.screen.pickphoto

import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apero.pickphoto.R
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.base.BaseComposeActivity
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import com.apero.pickphoto.internal.designsystem.LocalCustomTypography
import com.apero.pickphoto.internal.designsystem.component.VslTextView
import com.apero.pickphoto.internal.designsystem.pxToDp
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.NAME_ALL_PHOTOS
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoIntent
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoState
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoViewModel
import com.apero.pickphoto.internal.ui.screen.pickphoto.widget.PickPhotoItem
import com.apero.pickphoto.internal.ui.widgets.PickPhotoImage
import java.lang.ref.WeakReference

internal class PickPhotoActivity : BaseComposeActivity() {

    private val viewModel by lazy {
        DIContainer.viewModelContainer.getViewModel(this, PickPhotoViewModel::class.java)
    }

    override fun onBack() {
        DIContainer.vslPickPhotoActionConfig.actionBack(WeakReference(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onEvent(PickPhotoIntent.LoadInitPhotos(this@PickPhotoActivity))
        viewModel.onEvent(PickPhotoIntent.LoadFolders(this@PickPhotoActivity))
    }

    @Composable
    override fun SetupUi() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        PickPhotoScreen(
            uiState = uiState,
            onNext = {
                DIContainer.vslPickPhotoActionConfig.actionAfterApprove(
                    "content://media/" + it,
                    WeakReference(this)
                )
            },
            onBackPressed = {
                onBack()
            },
            onPhotoSelected = {
                viewModel.onEvent(PickPhotoIntent.SelectPhoto(it))
            },
            onFolderSelected = {
                viewModel.onEvent(PickPhotoIntent.SelectFolder(it))
            },
            onClickShowListFolder = {
                viewModel.onEvent(PickPhotoIntent.ChangeShowListFolder)
            }
        )
    }
}


@Composable
internal fun PickPhotoScreen(
    uiState: PickPhotoState,
    onNext: (String?) -> Unit,
    onBackPressed: () -> Unit,
    onFolderSelected: (PhotoFolderModel) -> Unit,
    onPhotoSelected: (PhotoModel) -> Unit,
    onClickShowListFolder: () -> Unit,
) {
    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(
                        top = 17.pxToDp(),
                        bottom = 13.pxToDp(),
                        start = 16.pxToDp(),
                        end = 16.pxToDp()
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vsl_ic_close),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onBackPressed.invoke()
                    }
                )
                TitlePickPhoto {
                    onClickShowListFolder.invoke()
                }
                VslTextView(
                    text = stringResource(R.string.vsl_pick_photo_next),
                    textStyle = LocalCustomTypography.current.Headline.semiBold,
                    modifier = Modifier.clickable {
                        onNext.invoke(uiState.itemSelected.path)
                    }
                )
            }
        },
        content = { paddingValues ->
            val scrollState = rememberScrollState()
            val itemSize = remember { 100.pxToDp() }
            val listFolderPadding = remember { 8.pxToDp() }
            if (uiState.photos.isEmpty()) {
                Box {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(20.pxToDp()),
                    horizontalArrangement = Arrangement.spacedBy(10.pxToDp()),
                    verticalArrangement = Arrangement.spacedBy(12.pxToDp()),
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(paddingValues)
                ) {
                    itemsIndexed(
                        items = if (uiState.folderSelected.folderId == NAME_ALL_PHOTOS) uiState.photos else uiState.folderSelected.photos,
                        key = { _, item -> item.path.toString() }) { _, it ->
                        PickPhotoItem(
                            it.uri,
                            modifier = Modifier.size(itemSize),
                            isSelected = if (uiState.folderSelected.folderId == NAME_ALL_PHOTOS) uiState.itemSelected.path == it.path else uiState.itemSelected.path == it.path
                        ) {
                            onPhotoSelected.invoke(it)
                        }
                    }
                }

            }

            if (uiState.isShowListFolder) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White)
                        .verticalScroll(scrollState)
                        .padding(paddingValues)
                        .padding(start = listFolderPadding)
                ) {
                    uiState.folders.forEach { folder ->
                        FolderPickPhoto(
                            folder.thumbnailUri,
                            folder.photos.size,
                            folder.folderName
                        ) {
                            onFolderSelected.invoke(folder)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.pxToDp()))
                }
            }
        }
    )
}

@Composable
fun TitlePickPhoto(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.clickable {
            onClick.invoke()
        }
    ) {
        VslTextView(
            text = stringResource(R.string.vsl_pick_photo_title),
            textStyle = LocalCustomTypography.current.Headline.semiBold,
        )
        Image(
            painter = painterResource(R.drawable.vsl_ic_more_folder),
            contentDescription = null
        )
    }
}

@Composable
fun FolderPickPhoto(
    url: Uri?,
    numberPhotoInFolder: Int,
    nameFolder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val sizeImageFolder = remember { 50.pxToDp() }
    val paddingRow = remember { 8.pxToDp() }
    val pickPhotoImagePadding = remember { 6.pxToDp() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }
            .padding(vertical = paddingRow)
    ) {
        PickPhotoImage(
            image = url,
            modifier = Modifier
                .width(sizeImageFolder)
                .height(sizeImageFolder)
                .padding(end = pickPhotoImagePadding)
        )
        VslTextView(
            text = "$nameFolder($numberPhotoInFolder)",
            textAlign = TextAlign.Center,
            textStyle = LocalCustomTypography.current.Headline.semiBold,
        )
    }
}

@Preview
@Composable
internal fun PreviewPickPhotoScreen() {
    PickPhotoScreen(uiState = PickPhotoState(), {}, {}, {}, {}, {})
}