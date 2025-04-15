package com.apero.pickphoto.internal.ui.screen.pickphoto.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.apero.pickphoto.R
import com.apero.pickphoto.internal.designsystem.LocalCustomTypography
import com.apero.pickphoto.internal.designsystem.component.VslTextView
import com.apero.pickphoto.internal.designsystem.pxToDp
import com.apero.pickphoto.internal.ui.widgets.PickPhotoImage
import kotlin.math.roundToInt

@Composable
internal fun PickPhotoItem(
    image: Any?,
    content: String = "",
    isSelected: Boolean = false,
    isShowSelectItem: Boolean = true,
    isShowBlur: Boolean = false,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val shape = remember { RoundedCornerShape(16.dp) }
    var parentSize by remember { mutableStateOf(Offset(0f, 0f)) }
    val iconSize = remember(parentSize) {
        (parentSize.x * 0.08f).pxToDp()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .clickable {
                onSelect.invoke()
            }
            .onSizeChanged { size ->
                parentSize = Offset(size.width.toFloat(), size.height.toFloat())
            }) {
        key(image) {
            PickPhotoImage(
                image, modifier = Modifier
                    .clip(shape)
                    .graphicsLayer(clip = true)
            )
        }
        if (isShowSelectItem) {
            Image(
                painter = painterResource(if (isSelected) R.drawable.vsl_ic_selected else R.drawable.vsl_ic_unselect),
                contentDescription = null,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (parentSize.x * 0.68f).roundToInt(),
                            (parentSize.y * 0.08f).roundToInt()
                        )
                    }
                    .size(iconSize)

            )
        }
        if(isShowBlur) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(25f / 8f)
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 0.5f))
                        .blur(4.dp)
                )

                VslTextView(
                    text = content,
                    textAlign = TextAlign.Center,
                    textStyle = LocalCustomTypography.current.Headline.medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
internal fun PickPhotoItemOption(
    image: Any?,
    content: String,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val shape = remember { RoundedCornerShape(16.dp) }
    var parentSize by remember { mutableStateOf(Offset(0f, 0f)) }
    val iconSize = remember(parentSize) {
        (parentSize.x * 0.16f).pxToDp()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .clickable {
                onSelect.invoke()
            }
            .background(color = colorResource(R.color.vsl_pick_photo_bg_item_camera))
            .onSizeChanged { size ->
                parentSize = Offset(size.width.toFloat(), size.height.toFloat())
            }) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(59f / 68f)
                .padding(vertical = 16.pxToDp() , horizontal = 20.pxToDp())
                .align(Alignment.Center)
        ) {
            key(image) {
                PickPhotoImage(
                    image, modifier = Modifier
                        .clip(shape)
                        .graphicsLayer(clip = true)
                        .size(iconSize)
                )
            }

            VslTextView(
                text = content,
                textAlign = TextAlign.Center,
                textStyle = LocalCustomTypography.current.Headline.medium,
            )
        }


    }
}

@Preview
@Composable
fun PreviewPickPhotoItem() {
    PickPhotoItem(image = null, isSelected = true) {}
}