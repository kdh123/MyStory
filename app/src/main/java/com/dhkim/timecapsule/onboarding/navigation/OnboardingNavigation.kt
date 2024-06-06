package com.dhkim.timecapsule.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.onboarding.signup.SignUpScreen
import com.dhkim.timecapsule.onboarding.splash.SplashScreen

fun NavGraphBuilder.onboardingNavigation(
    onNavigateToSignUp: () -> Unit
) {
    composable("splash") {
        SplashScreen(onNavigateToSignUp)
    }

    composable("signUp") {
        SignUpScreen()
    }
}