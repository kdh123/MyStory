package com.dhkim.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dhkim.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen()
        }
    }
}