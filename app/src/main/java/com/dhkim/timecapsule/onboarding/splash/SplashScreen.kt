package com.dhkim.timecapsule.onboarding.splash

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.timecapsule.main.MainActivity
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.ui.DefaultBackground
import com.dhkim.timecapsule.onboarding.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToSignUp: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isSignedUp by viewModel.isSignUp.collectAsStateWithLifecycle()

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
                    .align(Alignment.Center)
            )
        }
    }

    LaunchedEffect(true) {
        viewModel.checkSignedUp()
    }

    LaunchedEffect(isSignedUp) {
        delay(500L)
        isSignedUp?.let {
            if (it) {
                Intent(context, MainActivity::class.java).run {
                    context.startActivity(this)
                }
                (context as? Activity)?.finish()
            } else {
                onNavigateToSignUp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    //SplashScreen()
}