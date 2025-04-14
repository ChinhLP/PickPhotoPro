package com.apero.pickphoto.internal.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.apero.pickphoto.R

@Composable
fun PickPhotoImage(image: Any?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val placeholder = painterResource(R.drawable.img_place_holder)
    val error = painterResource(R.drawable.img_error)
    Box(modifier = modifier) {

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(image)
                .crossfade(true)
                .size(200)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = placeholder,
            error = error
        )
    }
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