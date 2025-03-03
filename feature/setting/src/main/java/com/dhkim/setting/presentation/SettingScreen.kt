package com.dhkim.setting.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.setting.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingScreen(
    uiState: SettingUiState,
    onNotificationChanged: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_black),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onBack()
                        }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(top = it.calculateTopPadding(), start = 10.dp, end = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = "알림",
                    style = MyStoryTheme.typography.titleMedium,
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Switch(
                    checked = uiState.isNotificationChecked,
                    onCheckedChange = { onNotificationChanged(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.primary),
                        checkedTrackColor = colorResource(id = R.color.teal_200),
                        uncheckedTrackColor = colorResource(id = R.color.gray),
                        uncheckedBorderColor = colorResource(id = R.color.gray),
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Divider()

            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://sites.google.com/view/mystorytimecapsule/")
                        )
                        context.startActivity(browserIntent)
                    }
            ) {
                Text(
                    text = "개인 정보 처리 방침",
                    style = MyStoryTheme.typography.titleMedium,
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingScreen(
                uiState = SettingUiState(),
                onNotificationChanged = {},
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SettingScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingScreen(
                uiState = SettingUiState(),
                onNotificationChanged = {},
                onBack = {}
            )
        }
    }
}