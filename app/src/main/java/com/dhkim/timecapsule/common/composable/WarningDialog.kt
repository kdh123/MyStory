package com.dhkim.timecapsule.common.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.dhkim.timecapsule.R

@Composable
fun WarningDialog(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    iconResId: Int = R.drawable.ic_warning_yellow,
    choice: Boolean = true
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null
            )
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            if (choice) {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text("예")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                if (choice) {
                    Text("아니오")
                } else {
                    Text("확인")
                }
            }
        }
    )
}