package com.dhkim.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.dhkim.designsystem.MyStoryTheme

@Composable
fun WarningDialog(
    dialogTitle: String,
    dialogText: String,
    iconResId: Int = R.drawable.ic_warning_yellow,
    choice: Boolean = true,
    negativeText: String = "아니오",
    positiveText: String = "예",
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null
            )
        },
        title = {
            Text(
                text = dialogTitle,
                style = MyStoryTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = dialogText,
                style = MyStoryTheme.typography.bodyMedium
            )
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            if (choice) {
                TextButton(
                    onClick = onConfirmation
                ) {
                    Text(
                        text = positiveText,
                        style = MyStoryTheme.typography.labelLarge
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = if (choice) negativeText else "확인",
                    style = MyStoryTheme.typography.labelLarge
                )
            }
        }
    )
}