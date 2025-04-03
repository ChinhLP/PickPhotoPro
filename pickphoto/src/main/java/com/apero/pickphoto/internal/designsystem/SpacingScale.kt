package com.apero.pickphoto.internal.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val LocalScreenScale = compositionLocalOf { 1f }

@Composable
internal fun Number.pxToDpCompose(): Dp {
    val scale = LocalScreenScale.current
    return (this.toFloat() * scale).dp
}

internal fun Number.pxToDp(): Dp {
    val scale = cachedDensityScale
    return (this.toFloat() * scale).dp
}
