package com.dhkim.timecapsule.onboarding.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.onboarding.signup.SignUpScreen
import com.dhkim.timecapsule.onboarding.signup.SignUpSideEffect
import com.dhkim.timecapsule.onboarding.signup.SignUpViewModel
import com.dhkim.timecapsule.onboarding.splash.SplashScreen

fun NavGraphBuilder.onboardingNavigation(
    onNavigateToSignUp: () -> Unit
) {
    composable("splash") {
        SplashScreen(onNavigateToSignUp)
    }

    composable("signUp") {
        val viewModel = hiltViewModel<SignUpViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = SignUpSideEffect.None)

        SignUpScreen(
            uiState = uiState,
            sideEffect = sideEffect,
            onQuery = viewModel::onQuery,
            onProfileSelect = viewModel::onProfileSelect,
            onSignUp = viewModel::signUp
        )
    }
}