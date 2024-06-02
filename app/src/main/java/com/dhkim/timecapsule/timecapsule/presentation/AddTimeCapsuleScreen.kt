package com.dhkim.timecapsule.timecapsule.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AddTimeCapsuleScreen(imageUrl: String) {
    Box {
        Text("imageUrl: $imageUrl")
    }
}