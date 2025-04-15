package com.apero.pickphoto.internal.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.disk.DiskCache
import coil3.network.NetworkFetcher
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.apero.pickphoto.R
import okio.Path.Companion.toOkioPath

@NonRestartableComposable
@Composable
fun PickPhotoImage(image: Any?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val placeholder = painterResource(R.drawable.img_place_holder)
    val error = painterResource(R.drawable.img_error)
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .allowHardware(false)
            .memoryCachePolicy(CachePolicy.ENABLED) // Bật cache
            .diskCache {
                val path = context.filesDir.resolve("COIL_DIR").toOkioPath()
                DiskCache.Builder()
                    .maxSizePercent(0.2) // Tối ưu bộ nhớ cache
                    .directory(path)
                    .build()
            }
            .build()
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(image)
            .size(450)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .crossfade(false)
            .build(),
        imageLoader = imageLoader,
        placeholder = placeholder,
        error = error
    )
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize()
    )
}

@Preview
@Composable
fun PreviewPickPhotoImage() {
    PickPhotoImage(
        image = "https://example.com/image.jpg",
        modifier = Modifier
            .size(100.dp)
    )
}