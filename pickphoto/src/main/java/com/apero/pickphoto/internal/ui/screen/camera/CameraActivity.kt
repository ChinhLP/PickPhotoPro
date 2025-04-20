package com.apero.pickphoto.internal.ui.screen.camera

import android.net.Uri
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.apero.pickphoto.R
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.base.BaseComposeActivity
import com.apero.pickphoto.internal.designsystem.pxToDp
import com.apero.pickphoto.internal.ui.screen.camera.intent.CameraIntent
import com.apero.pickphoto.internal.ui.screen.camera.intent.CameraViewModel
import java.io.File
import java.lang.ref.WeakReference

internal class CameraActivity : BaseComposeActivity() {
    private val cameraViewModel by lazy {
        DIContainer.viewModelContainer.getViewModel(this, CameraViewModel::class.java)
    }

    // Thêm biến để lưu trạng thái camera
    private var lastCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onBack() {
        if (cameraViewModel.cameraUiState.value.uriImage != null) {
            cameraViewModel.onEvent(CameraIntent.SetUriImageCapture(null))
        } else {
            finish()
        }
    }

    @Composable
    override fun SetupUi() {
        val uiState by cameraViewModel.cameraUiState.collectAsStateWithLifecycle()
        if (uiState.uriImage != null) {
            ImagePreviewScreen(
                imageUri = uiState.uriImage!!,
                onAccept = {
                    DIContainer.vslPickPhotoActionConfig.actionAfterApprove(
                        uiState.uriImage!!.path,
                        WeakReference(this)
                    )
                    cameraViewModel.onEvent(
                        intent = CameraIntent.SetUriImageCapture(null)
                    )
                    finish()
                },
                onReject = {
                    cameraViewModel.onEvent(CameraIntent.SetUriImageCapture(null))
                }
            )
        } else {
            CameraScreen(
                onImageCaptured = { uri ->
                    cameraViewModel.onEvent(CameraIntent.SetUriImageCapture(uri))
                },
                onError = { exception ->
                    finish()
                },
                initialCameraSelector = lastCameraSelector,  // Truyền camera selector đã lưu
                onCameraSelectorChanged = { selector ->
                    lastCameraSelector = selector
                }
            )
        }
    }

    @Composable
    fun ImagePreviewScreen(
        imageUri: Uri,
        onAccept: () -> Unit,
        onReject: () -> Unit
    ) {
        val imageCapture = rememberAsyncImagePainter(
            model = imageUri,
        )
        var isProcessingClick by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                Image(
                    painter = imageCapture,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(vertical = 24.pxToDp()),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (!isProcessingClick) {
                                isProcessingClick = true
                                onReject()
                            }
                        },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(64.pxToDp())
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            if (!isProcessingClick) {
                                isProcessingClick = true
                                onAccept()
                            }
                        },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.size(64.pxToDp())
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Chấp nhận"
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun CameraScreen(
        modifier: Modifier = Modifier,
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit,
        initialCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
        onCameraSelectorChanged: (CameraSelector) -> Unit = {}
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var isProcessingCapture by remember { mutableStateOf(false) }

        var cameraSelector by remember { mutableStateOf(initialCameraSelector) }
        // Thêm biến để theo dõi zoom
        var camera by remember { mutableStateOf<Camera?>(null) }
        var cameraZoom by remember { mutableStateOf(1f) }
        // Lưu giới hạn zoom tối đa
        var maxZoom by remember { mutableStateOf(1f) }
        // Lưu giới hạn zoom tối thiểu
        var minZoom by remember { mutableStateOf(1f) }

        LaunchedEffect(cameraSelector) {
            onCameraSelectorChanged(cameraSelector)
        }

        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val previewView = remember { PreviewView(context) }

        val imageCapture = remember {
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(android.view.Surface.ROTATION_0)
                .build()
        }

        Column(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            // Thêm gesture detector cho pinch zoom
                            detectTransformGestures { _, _, zoom, _ ->
                                val currentZoom = cameraZoom * zoom
                                cameraZoom = currentZoom.coerceIn(minZoom, maxZoom)
                                camera?.cameraControl?.setZoomRatio(cameraZoom)
                            }
                        }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black)
            ) {
                CameraControls(
                    modifier = Modifier.align(Alignment.Center),
                    onCaptureClicked = {
                        if (!isProcessingCapture) {
                            isProcessingCapture = true
                            captureImage(
                                context,
                                imageCapture,
                                cameraSelector,
                                { uri ->
                                    onImageCaptured(uri)
                                },
                                { error ->
                                    onError(error)
                                    isProcessingCapture = false
                                }
                            )
                        }
                    },
                    onSwitchCamera = {
                        if (!isProcessingCapture) {
                            cameraSelector =
                                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                        }
                    }
                )
            }
        }

        LaunchedEffect(cameraSelector) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                // Bổ sung camera vào các kết quả binding
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
                
                // Khởi tạo các giá trị zoom
                camera?.cameraInfo?.zoomState?.value?.let { zoomState ->
                    minZoom = zoomState.minZoomRatio
                    maxZoom = zoomState.maxZoomRatio
                    cameraZoom = 1f // Reset về 1f khi thay đổi camera
                }
            } catch (e: Exception) {
                onError(
                    ImageCaptureException(
                        ImageCapture.ERROR_UNKNOWN,
                        "Failed to bind camera",
                        e
                    )
                )
            }
        }
    }

    @Composable
    private fun CameraControls(
        modifier: Modifier = Modifier,
        onCaptureClicked: () -> Unit,
        onSwitchCamera: () -> Unit
    ) {
        var isCaptureProcessing by remember { mutableStateOf(false) }
        var isSwitchProcessing by remember { mutableStateOf(false) }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 24.pxToDp()),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Khoảng trống để giữ cân đối giao diện
            Box(modifier = Modifier.size(20.pxToDp()))
            
            Box(
                modifier = Modifier
                    .size(64.pxToDp())
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(
                        onClick = {
                            if (!isCaptureProcessing) {
                                isCaptureProcessing = true
                                onCaptureClicked()
                            }
                        },
                        role = Role.Button,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.vsl_ic_apture),
                    modifier = Modifier.size(20.pxToDp()),
                    contentDescription = null
                )
            }
            
            IconButton(
                onClick = {
                    if (!isSwitchProcessing) {
                        isSwitchProcessing = true
                        onSwitchCamera()
                        isSwitchProcessing = false
                    }
                },
                modifier = Modifier
                    .size(20.pxToDp())
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.vsl_ic_switch_camera),
                    modifier = Modifier.size(20.pxToDp()),
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }

        }
    }

    private fun captureImage(
        context: android.content.Context,
        imageCapture: ImageCapture,
        cameraSelector: CameraSelector,
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val photoFile = File(
            context.externalCacheDirs.first(),
            "IMAGE_PREVIEW_IMG_${System.currentTimeMillis()}.jpg"
        )

        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = Uri.fromFile(photoFile)
                    onImageCaptured(uri)
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    @Composable
    fun PreviewTheme(content: @Composable () -> Unit) {
        content()
    }

    @androidx.compose.ui.tooling.preview.Preview(
        name = "Image Preview Screen",
        showBackground = true,
        device = androidx.compose.ui.tooling.preview.Devices.PIXEL_4
    )
    @Composable
    fun ImagePreviewScreenPreview() {
        val mockUri = Uri.parse("content://mock/uri")
        PreviewTheme {
            ImagePreviewScreen(
                imageUri = mockUri,
                onAccept = {},
                onReject = {}
            )
        }
    }

    @androidx.compose.ui.tooling.preview.Preview(
        name = "Camera Screen",
        showBackground = true,
        device = androidx.compose.ui.tooling.preview.Devices.PIXEL_4
    )
    @Composable
    fun CameraScreenPreview() {
        PreviewTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.67f)
                        .background(Color.LightGray)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.Black)
                ) {
                    CameraControls(modifier = Modifier.align(Alignment.Center), {}, {})
                }
            }
        }
    }

    @androidx.compose.ui.tooling.preview.Preview(
        name = "Camera Controls",
        showBackground = true,
        backgroundColor = 0xFF000000,
        showSystemUi = false
    )
    @Composable
    fun CameraControlsPreview() {
        PreviewTheme {
            CameraControls(
                onCaptureClicked = {},
                onSwitchCamera = {},
            )
        }
    }

    @androidx.compose.ui.tooling.preview.Preview(
        name = "Phone",
        device = androidx.compose.ui.tooling.preview.Devices.PIXEL_4,
        showSystemUi = true
    )
    @androidx.compose.ui.tooling.preview.Preview(
        name = "Tablet",
        showSystemUi = true
    )
    @Composable
    fun MultiDevicePreview() {
        PreviewTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                ImagePreviewScreen(
                    imageUri = Uri.parse("content://mock/uri"),
                    onAccept = {},
                    onReject = {}
                )
            }
        }
    }
}