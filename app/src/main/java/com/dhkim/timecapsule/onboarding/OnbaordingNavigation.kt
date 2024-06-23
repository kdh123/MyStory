package com.dhkim.timecapsule.onboarding

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.signUp.SignUpScreen
import com.dhkim.timecapsule.signUp.SignUpSideEffect
import com.dhkim.timecapsule.signUp.SignUpViewModel
import com.dhkim.timecapsule.splash.SplashScreen
import com.dhkim.timecapsule.splash.SplashViewModel

fun NavGraphBuilder.onboardingNavigation(
    onNavigateToSignUp: () -> Unit
) {
    composable("splash") {
        val viewModel = hiltViewModel<SplashViewModel>()
        val isSignedUp by viewModel.isSignUp.collectAsStateWithLifecycle()

        SplashScreen(
            isSignedUp = isSignedUp,
            onCheckSignedUp = viewModel::checkSignedUp,
            onNavigateToSignUp = onNavigateToSignUp
        )
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