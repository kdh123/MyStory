package com.dhkim.timecapsule.onboarding.signup

data class SignUpUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val errorMessage: String = ""
)