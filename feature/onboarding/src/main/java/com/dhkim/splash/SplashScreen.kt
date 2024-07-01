package com.dhkim.splash

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dhkim.main.MainActivity
import com.dhkim.onboarding.R
import com.dhkim.ui.DefaultBackground
import com.dhkim.ui.LoadingProgressBar
import com.dhkim.ui.WarningDialog

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    sideEffect: SplashSideEffect
) {
    val context = LocalContext.current
    var showWarningDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DefaultBackground(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_send_time_white),
                contentDescription = null,
                modifier = Modifier
                    .padding(70.dp)
                    .fillMaxSize()
            )

            if (uiState.isLoading) {
                LoadingProgressBar(
                    modifier = Modifier
                        .padding(bottom = 80.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }

    if (showWarningDialog) {
        WarningDialog(
            dialogTitle = "에러",
            dialogText = "앱 실행에 실패하였습니다.",
            choice = false,
            onConfirmation = {
                (context as? Activity)?.finish()
            },
            onDismissRequest = {
                (context as? Activity)?.finish()
            }
        )
    }

    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is SplashSideEffect.Completed -> {
                if (sideEffect.isCompleted) {
                    Intent(context, MainActivity::class.java).run {
                        context.startActivity(this)
                    }
                    (context as? Activity)?.finish()
                }
            }

            is SplashSideEffect.ShowPopup -> {
                showWarningDialog = true
            }

            is SplashSideEffect.None -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        uiState = SplashUiState(),
        sideEffect = SplashSideEffect.None
    )
}