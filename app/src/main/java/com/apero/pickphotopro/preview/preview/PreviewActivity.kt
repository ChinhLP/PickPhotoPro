package com.apero.pickphotopro.preview.preview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.apero.pickphotopro.R
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

class PreviewActivity : ComponentActivity() {
    companion object {
        const val IMAGE_PREVIEW = "image_preview"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUrl = intent.getStringExtra(IMAGE_PREVIEW)
        setContent {
            PreviewScreen(imageUrl) {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}

@Composable
fun PreviewScreen(url: String?, backClick: () -> Unit) {
    Scaffold(
        topBar = {
            Image(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
        },
        content = { paddingValues ->
            AvatarImage(
                url, modifier = Modifier
                    .padding(paddingValues)
                    .clickable {
                        backClick.invoke()
                    })
        }
    )
}

@Composable
fun AvatarImage(url: String?, modifier: Modifier) {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    val painter = rememberAsyncImagePainter(model = url)
    val state = painter.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(CircleShape)
    ) {
        if (state.value is AsyncImagePainter.State.Loading)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .shimmer(shimmerInstance) // Hiệu ứng shimmer loading
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
    }

    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        placeholder = painterResource(id = R.drawable.img_place_holder),
        error = painterResource(id = R.drawable.imgError)
    )
}

@Preview
@Composable
fun Preview() {
    PreviewScreen("https://static.wikia.nocookie.net/reverend-insanity/images/b/b4/Yuan11.jpg/revision/latest?cb=20250111162706",
        {})
}