package com.apero.pickphoto.internal.ui.screen.pickphoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.apero.pickphoto.internal.ui.screen.camera.CameraActivity
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.NAME_ALL_PHOTOS
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoIntent
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoState
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoViewModel
import com.apero.pickphoto.internal.ui.widgets.PickPhotoDialogPermission
import com.apero.pickphoto.internal.ui.screen.pickphoto.widget.PickPhotoItem
import com.apero.pickphoto.internal.ui.screen.pickphoto.widget.PickPhotoItemOption
import com.apero.pickphoto.internal.ui.widgets.PickPhotoImage
import com.apero.pickphoto.util.PermissionUtil
import com.apero.pickphoto.util.PermissionUtil.Companion.TYPE_PERMISSION_CAMERA
import com.apero.pickphoto.util.PermissionUtil.Companion.TYPE_PERMISSION_GALLERY
import kotlinx.coroutines.flow.StateFlow

import java.lang.ref.WeakReference

internal class PickPhotoActivity : BaseComposeActivity() {

    private val viewModel by lazy {
        DIContainer.viewModelContainer.getViewModel(this, PickPhotoViewModel::class.java)
    }

    private val permissionUtil by lazy {
        PermissionUtil(DIContainer.repositoryContainer.sharedPref)
    }

    private val requestPermissionsPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissionUtil.checkPermissionsPhoto(WeakReference(this@PickPhotoActivity))) {
                viewModel.onEvent(PickPhotoIntent.SetPhotoPermissionFullGranted(true))
                viewModel.onEvent(PickPhotoIntent.LoadInitPhotos(this@PickPhotoActivity))
                viewModel.onEvent(PickPhotoIntent.LoadFolders(this@PickPhotoActivity))
            }
        }

    private val requestPermissionCameraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

    override fun onBack() {
        DIContainer.vslPickPhotoActionConfig.actionBack(WeakReference(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (permissionUtil.checkPermissionsPhoto(WeakReference(this))) {
            viewModel.onEvent(PickPhotoIntent.LoadInitPhotos(this@PickPhotoActivity))
            viewModel.onEvent(PickPhotoIntent.LoadFolders(this@PickPhotoActivity))
            viewModel.onEvent(PickPhotoIntent.SetPhotoPermissionFullGranted(true))
        } else {
            permissionUtil.requestPermissionsPhoto(
                WeakReference(this),
                requestPermissionsPhotoLauncher
            )
        }
    }

    @Composable
    override fun SetupUi() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val showDialogCustomPermission = remember { mutableStateOf(false) }
        val typePermissionsRequest = remember { mutableStateOf("") }
        val activityResultLauncher = rememberSettingsPermissionLauncher(typePermissionsRequest)



        PickPhotoDialogPermission(
            shouldShowDialog = showDialogCustomPermission.value,
            stringResourceContent = when (typePermissionsRequest.value) {
                TYPE_PERMISSION_GALLERY -> R.string.vsl_pick_photo_content_dialog_permission_photo
                TYPE_PERMISSION_CAMERA -> R.string.vsl_pick_photo_content_dialog_permission_camera
                else -> R.string.vsl_pick_photo_content_dialog_permission_photo
            },
            onConfirm = {
                showDialogCustomPermission.value = false
                openAppSettings(activityResultLauncher)
            },
            onDismiss = {
                showDialogCustomPermission.value = false
            }
        )

        PickPhotoScreen(
            uiState = uiState,
            onRequestPermission = {
                handlePhotoPermissionRequest(
                    typePermissionsRequest,
                    showDialogCustomPermission
                )
            },
            onOpenCamera = {
                handleCameraAction(
                    typePermissionsRequest,
                    showDialogCustomPermission
                )
            },
            onNext = { handleNextAction(it, uiState) },
            onBackPressed = { onBack() },
            onPhotoSelected = { viewModel.onEvent(PickPhotoIntent.SelectPhoto(it)) },
            onFolderSelected = { viewModel.onEvent(PickPhotoIntent.SelectFolder(it)) },
            onClickShowListFolder = { viewModel.onEvent(PickPhotoIntent.ChangeShowListFolder) }
        )
    }

    @Composable
    private fun rememberSettingsPermissionLauncher(typePermissionsRequest: MutableState<String>) =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            if (typePermissionsRequest.value == TYPE_PERMISSION_GALLERY && permissionUtil.checkPermissionsPhoto(
                    WeakReference(this@PickPhotoActivity)
                )
            ) {
                viewModel.onEvent(PickPhotoIntent.SetPhotoPermissionFullGranted(true))
                viewModel.onEvent(PickPhotoIntent.LoadInitPhotos(this@PickPhotoActivity))
                viewModel.onEvent(PickPhotoIntent.LoadFolders(this@PickPhotoActivity))
            }
        }


    private fun openAppSettings(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", this@PickPhotoActivity.packageName, null)
        }
        launcher.launch(intent)
    }

    private fun handlePhotoPermissionRequest(
        setPermissionType: MutableState<String>,
        shouldShowDialogCustomPermission: MutableState<Boolean>
    ) {
        permissionUtil.requestPermissionsPhoto(
            WeakReference(this),
            requestPermissionsPhotoLauncher
        ) { type ->
            setPermissionType.value = type
            shouldShowDialogCustomPermission.value = true
        }
    }

    private fun handleCameraAction(
        setPermissionType: MutableState<String>,
        shouldShowDialogCustomPermission: MutableState<Boolean>
    ) {
        if (permissionUtil.checkCameraPermission(WeakReference(this)).not()) {
            permissionUtil.requestCameraPermission(
                WeakReference(this),
                requestPermissionCameraLauncher,
            ) { type ->
                setPermissionType.value = type
                shouldShowDialogCustomPermission.value = true
            }
        } else {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleNextAction(path: String?, uiState: PickPhotoState) {
        DIContainer.vslPickPhotoActionConfig.actionAfterApprove(
            if (path == uiState.pathImageSample) path else "content://media/" + path,
            WeakReference(this)
        )
    }
}

@Composable
internal fun PickPhotoScreen(
    uiState: PickPhotoState,
    onOpenCamera: () -> Unit,
    onRequestPermission: () -> Unit,
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
            val paddingGrid = remember { 20.pxToDp() }
            val horizontalArrangementSpace = 10.pxToDp()
            val verticalArrangementSpace = remember { 12.pxToDp() }
            LazyVerticalGrid(
                contentPadding = PaddingValues(paddingGrid),
                horizontalArrangement = Arrangement.spacedBy(horizontalArrangementSpace),
                verticalArrangement = Arrangement.spacedBy(verticalArrangementSpace),
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(paddingValues)
            ) {
                item {
                    PickPhotoItemOption(
                        image = R.drawable.vsl_ic_camera,
                        modifier = Modifier.size(itemSize),
                        content = stringResource(R.string.vsl_pick_photo_label_demo)
                    ) {
                        onOpenCamera()
                    }
                }
                if (uiState.isFullPhotoPermissionGranted.not())
                    item {
                        PickPhotoItemOption(
                            image = R.drawable.vsl_ic_add_photo,
                            modifier = Modifier.size(itemSize),
                            content = stringResource(R.string.vsl_pick_photo_label_add_photo)
                        ) {
                            onRequestPermission()
                        }
                    }

                item {
                    PickPhotoItem(
                        R.drawable.img_demo,
                        modifier = Modifier.size(itemSize),
                        isSelected = uiState.itemSelected.path == uiState.pathImageSample
                    ) {
                        onPhotoSelected.invoke(
                            PhotoModel(
                                path = uiState.pathImageSample,
                                name = "Sample Photo"
                            )
                        )
                    }
                }

                if (uiState.photos.isNotEmpty()) {
                    itemsIndexed(
                        items = if (uiState.folderSelected.folderId == NAME_ALL_PHOTOS) uiState.photos else uiState.folderSelected.photos,
                        key = { _, item -> item.path.toString() }) { _, it ->
                        PickPhotoItem(
                            it.uri,
                            modifier = Modifier.size(itemSize),
                            isSelected = uiState.itemSelected.path == it.path
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

@NonRestartableComposable
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

@NonRestartableComposable
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
    PickPhotoScreen(uiState = PickPhotoState(), {}, {}, {}, {}, {}, {}, {})
}