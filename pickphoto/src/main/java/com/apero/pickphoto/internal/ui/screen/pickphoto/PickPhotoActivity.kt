package com.apero.pickphoto.internal.ui.screen.pickphoto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
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
import com.apero.pickphoto.internal.data.model.PhotoModel
import com.apero.pickphoto.internal.designsystem.LocalCustomTypography
import com.apero.pickphoto.internal.designsystem.component.VslTextView
import com.apero.pickphoto.internal.designsystem.pxToDp
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
                viewModel.onEvent(PickPhotoIntent.SelectPhoto(it))
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
    onLoadMorePickPhotos: () -> Unit,
    onLoadMorePhotoInFolder: () -> Unit,
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
                    contentDescription = null
                )
                TitlePickPhoto {
                    onClickShowListFolder.invoke()
                }
                VslTextView(
                    text = stringResource(R.string.vsl_pick_photo_next),
                    textStyle = LocalCustomTypography.current.Headline.semiBold,
                )
            }
        },
        content = { paddingValues ->
            val scrollState = rememberScrollState()
            val gridState = rememberLazyGridState()
            val itemSize = remember { 100.pxToDp() }

            LaunchedEffect(gridState) {
                snapshotFlow { gridState.layoutInfo }
                    .collect { layoutInfo ->
                        val totalItems = layoutInfo.totalItemsCount
                        val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                        // Kiểm tra nếu đã cuộn đến gần cuối (ví dụ: cách cuối 5 item)
                        if (lastVisibleItem >= totalItems - 6) {
                            onLoadMorePickPhotos.invoke() // gọi load thêm dữ liệu
                        }
                    }
            }
            LazyVerticalGrid(
                state = gridState,
                contentPadding = PaddingValues(20.pxToDp()),
                horizontalArrangement = Arrangement.spacedBy(10.pxToDp()),
                verticalArrangement = Arrangement.spacedBy(12.pxToDp()),
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(paddingValues)
            ) {
                items(items = uiState.photos, key = { it.id.toString() }) {
                    PickPhotoItem(
                        it.uri,
                        modifier = Modifier.size(itemSize),
                        isSelected = uiState.itemSelected.id == it.id
                    ) {
                        onPhotoSelected.invoke(it)
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
                        .padding(start = 8.pxToDp())
                ) {
                    FolderPickPhoto("", "All Photos") {}
                    FolderPickPhoto("", "Face Book") {}
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
    url: String,
    nameFolder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }
            .padding(vertical = 8.pxToDp())
    ) {
        PickPhotoImage(
            image = url,
            modifier = Modifier
                .width(50.pxToDp())
                .height(50.pxToDp())
                .padding(end = 6.pxToDp())
        )
        VslTextView(
            text = nameFolder,
            textAlign = TextAlign.Center,
            textStyle = LocalCustomTypography.current.Headline.semiBold,
        )
    }
}

@Preview
@Composable
internal fun PreviewPickPhotoScreen() {
    PickPhotoScreen(uiState = PickPhotoState(), {}, {}, {}, {})
}