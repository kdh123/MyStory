package com.dhkim.timecapsule.signUp.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.signUp.SignUpScreen
import com.dhkim.timecapsule.signUp.SignUpSideEffect
import com.dhkim.timecapsule.signUp.SignUpViewModel

fun NavController.navigateToSignUp() {
    navigate("signUp") {
        popUpTo("splash") {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.signUpNavigation() {
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