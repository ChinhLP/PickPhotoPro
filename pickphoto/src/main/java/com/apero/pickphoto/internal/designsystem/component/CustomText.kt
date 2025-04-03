package com.apero.pickphoto.internal.designsystem.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apero.pickphoto.internal.designsystem.LocalCustomTypography
import com.apero.pickphoto.internal.designsystem.pxToDp

@Composable
internal fun VslTextView(
    text: String,
    textStyle: TextStyle,
    maxLines: Int? = null,
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier,
    marqueeEnabled: Boolean = false
) {
    Box(
        modifier = modifier
            .height(
                textStyle.lineHeight.value.pxToDp() *
                        (maxLines ?: (text.count { it == '\n' } + 1))
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = textStyle,
            maxLines = maxLines ?: Int.MAX_VALUE,
            textAlign = textAlign,
            overflow = if (maxLines != null) TextOverflow.Ellipsis else TextOverflow.Clip,
            modifier = if (marqueeEnabled) {
                Modifier.basicMarquee(
                    iterations = Int.MAX_VALUE,
                )
            } else {
                Modifier
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun VslTextViewPreview() {
    Column {
        // Preview with 2 lines max
        VslTextView(
            text = "This is a very long text that will definitely overflow and show ellipsis when it reaches more than two lines because it's too long to fit",
            textStyle = LocalCustomTypography.current.Headline.semiBold,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preview with unlimited lines
        VslTextView(
            text = "Line 1\nLine 2\nLine 3\nLine 4\nLine 5",
            textStyle = LocalCustomTypography.current.Headline.semiBold
        )
    }
}