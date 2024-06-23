package com.dhkim.timecapsule.common.ui

import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.dhkim.timecapsule.R

@Composable
fun LoadingProgressBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .width(64.dp),
        color = Color.White,
        trackColor = colorResource(id = R.color.primary),
    )
}