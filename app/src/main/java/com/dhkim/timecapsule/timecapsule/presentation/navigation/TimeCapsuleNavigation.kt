package com.dhkim.timecapsule.timecapsule.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.timecapsule.TimeCapsuleScreen
import com.dhkim.timecapsule.timecapsule.presentation.AddTimeCapsuleScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun NavGraphBuilder.timeCapsuleNavigation() {
    composable(Screen.TimeCapsule.route) {
        TimeCapsuleScreen()
    }
}

fun NavGraphBuilder.addTimeCapsuleNavigation() {
    composable("addTimeCapsule/{savedUrl}") { backStackEntry ->
        val savedUrl = (backStackEntry.arguments?.getString("savedUrl") ?: "")
        val url = URLDecoder.decode(
            savedUrl,
            StandardCharsets.UTF_8.toString()
        )

        AddTimeCapsuleScreen(url)
    }
}