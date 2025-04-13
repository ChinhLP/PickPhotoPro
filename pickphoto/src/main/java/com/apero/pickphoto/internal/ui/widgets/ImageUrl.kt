package com.apero.pickphoto.internal.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.apero.pickphoto.R
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun PickPhotoImage(image: Any?, modifier: Modifier = Modifier) {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    val painter = rememberAsyncImagePainter(model = image)
    val state = painter.state.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        if (state.value is AsyncImagePainter.State.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer(shimmerInstance)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        // Ảnh chính
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(), // luôn fit trong Box bên ngoài
            placeholder = painterResource(id = R.drawable.img_place_holder),
            error = painterResource(id = R.drawable.img_error)
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