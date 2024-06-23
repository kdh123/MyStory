package com.dhkim.timecapsule.common.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun DefaultBackground(modifier: Modifier = Modifier, content: @Composable (() -> Unit)? = null) {
    val brush = Brush.linearGradient(
        listOf(Color(0XFF3C5AFA), Color(0XFFF361DC))
    )

    Box {
        Canvas(
            modifier = modifier,
            onDraw = {
                drawRect(brush)
            }
        )
        content?.invoke()
    }
}