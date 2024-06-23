package com.dhkim.timecapsule.splash.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.splash.SplashViewModel
import com.dhkim.timecapsule.splash.SplashScreen

fun NavGraphBuilder.splashNavigation() {
    composable("splash") {
        val viewModel = hiltViewModel<SplashViewModel>()
        val isSignedUp by viewModel.isSignUp.collectAsStateWithLifecycle()

        SplashScreen(
            isSignedUp = isSignedUp
        )
    }
}