package com.apero.pickphoto.internal.ui.screen.pickphoto.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.apero.pickphoto.R
import com.apero.pickphoto.internal.designsystem.pxToDp
import com.apero.pickphoto.internal.ui.widgets.PickPhotoImage
import kotlin.math.roundToInt

@Composable
internal fun PickPhotoItem(
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    sonSelect: () -> Unit
) {
    var parentSize by remember { mutableStateOf(Offset(0f, 0f)) }
    Box(modifier = modifier
        .fillMaxSize()
        .clickable {
            sonSelect.invoke()
        }
        .onSizeChanged { size ->
            parentSize = Offset(size.width.toFloat(), size.height.toFloat())
        }) {
        PickPhotoImage(null, modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp)))
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
                .size((parentSize.x * 0.0576f).pxToDp())

        )
    }
}

@Preview
@Composable
fun PreviewPickPhotoItem() {
    PickPhotoItem(true) {}
}