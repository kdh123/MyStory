package com.dhkim.onboarding

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.signUp.SignUpScreen
import com.dhkim.signUp.SignUpSideEffect
import com.dhkim.signUp.SignUpViewModel
import com.dhkim.splash.SplashScreen
import com.dhkim.splash.SplashSideEffect
import com.dhkim.splash.SplashViewModel

fun NavGraphBuilder.onboardingNavigation() {
    composable("splash") {
        val viewModel = hiltViewModel<SplashViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = SplashSideEffect.None)

        SplashScreen(
            uiState = uiState,
            sideEffect = sideEffect
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