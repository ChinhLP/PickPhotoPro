package com.apero.pickphoto.internal.ui.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.apero.pickphoto.R
import com.apero.pickphoto.internal.designsystem.pxToDp

@Composable
fun PickPhotoDialogPermission(
    shouldShowDialog: Boolean,
    @StringRes stringResourceContent: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (shouldShowDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            modifier = Modifier.clip(RoundedCornerShape(16)),
            title = {
                Text(
                    text = stringResource(R.string.vsl_pick_photo_title_dialog_permission),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = stringResource(stringResourceContent),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                ) {
                    Text(
                        text = stringResource(R.string.vsl_pick_photo_confirm_dialog_permission),
                        color = Color(0xFF0D99FF)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                ) {
                    Text(
                        text = stringResource(R.string.vsl_pick_photo_dismiss_dialog_permission),
                        color = Color(0xFF0D99FF)
                    )
                }
            },
            shape = RoundedCornerShape(16.pxToDp())
        )
    }
}

@Preview
@Composable
fun PickPhotoDialogPermissionPreview() {
    PickPhotoDialogPermission(
        true,
        R.string.vsl_pick_photo_content_dialog_permission_photo,
        onDismiss = {},
        onConfirm = {})
}