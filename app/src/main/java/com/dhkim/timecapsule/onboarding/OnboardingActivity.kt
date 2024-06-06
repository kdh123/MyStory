package com.dhkim.timecapsule.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dhkim.timecapsule.onboarding.navigation.onboardingNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OnboardingScreen()
        }
    }
}

@Composable
fun OnboardingScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        onboardingNavigation(
            onNavigateToSignUp = {
                navController.navigate("signUp") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            }
        )
    }
}