package com.dhkim.timecapsule.splash

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dhkim.timecapsule.main.MainActivity
import com.dhkim.timecapsule.R
import com.dhkim.timecapsule.common.ui.DefaultBackground
import com.dhkim.timecapsule.signUp.navigation.navigateToSignUp
import com.dhkim.timecapsule.signUp.navigation.signUpNavigation
import com.dhkim.timecapsule.splash.navigation.splashNavigation
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isSignedUp: Boolean?,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "splash") {
        splashNavigation()
        signUpNavigation()
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
                    .align(Alignment.Center)
            )
        }
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
                navController.navigateToSignUp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        isSignedUp = false
    )
}