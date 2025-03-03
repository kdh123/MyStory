package com.dhkim.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SansSerifStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
)

internal val Typography = MyStoryTypography(
    displayLarge = SansSerifStyle.copy(
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = SansSerifStyle.copy(
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = SansSerifStyle.copy(
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = SansSerifStyle.copy(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineLargeBold = SansSerifStyle.copy(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = SansSerifStyle.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineMediumBold = SansSerifStyle.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = SansSerifStyle.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmallBold = SansSerifStyle.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = SansSerifStyle.copy(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = SansSerifStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = SansSerifStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelLarge = SansSerifStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = SansSerifStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = SansSerifStyle.copy(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyLarge = SansSerifStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyLargeBold = SansSerifStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Bold
    ),
    bodyLargeGray = SansSerifStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = Color.Gray
    ),
    bodyLargeBlack = SansSerifStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = Color.Black
    ),
    bodyLargeGrayBold = SansSerifStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Bold
    ),
    bodyLargeWhiteBold = SansSerifStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    ),
    bodyMedium = SansSerifStyle.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodyMediumGray = SansSerifStyle.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = Color.Gray
    ),
    bodyMediumBold = SansSerifStyle.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        fontWeight = FontWeight.Bold
    ),
    bodySmall = SansSerifStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    bodySmallBold = SansSerifStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        fontWeight = FontWeight.Bold
    ),
    bodySmallGray = SansSerifStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = Color.Gray
    )
)

val LocalTypography = staticCompositionLocalOf {
    MyStoryTypography(
        displayLarge = SansSerifStyle,
        displayMedium = SansSerifStyle,
        displaySmall = SansSerifStyle,
        headlineLarge = SansSerifStyle,
        headlineLargeBold = SansSerifStyle,
        headlineMedium = SansSerifStyle,
        headlineMediumBold = SansSerifStyle,
        headlineSmall = SansSerifStyle,
        headlineSmallBold = SansSerifStyle,
        titleLarge = SansSerifStyle,
        titleMedium = SansSerifStyle,
        titleSmall = SansSerifStyle,
        bodyLarge = SansSerifStyle,
        bodyLargeBlack = SansSerifStyle,
        bodyLargeGray = SansSerifStyle,
        bodyLargeBold = SansSerifStyle,
        bodyLargeGrayBold = SansSerifStyle,
        bodyLargeWhiteBold = SansSerifStyle,
        bodyMedium = SansSerifStyle,
        bodyMediumGray = SansSerifStyle,
        bodyMediumBold = SansSerifStyle,
        bodySmall = SansSerifStyle,
        bodySmallBold = SansSerifStyle,
        bodySmallGray = SansSerifStyle,
        labelLarge = SansSerifStyle,
        labelMedium = SansSerifStyle,
        labelSmall = SansSerifStyle,
    )
}

@Immutable
data class MyStoryTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val headlineLarge: TextStyle,
    val headlineLargeBold: TextStyle,
    val headlineMedium: TextStyle,
    val headlineMediumBold: TextStyle,
    val headlineSmall: TextStyle,
    val headlineSmallBold: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyLargeBlack: TextStyle,
    val bodyLargeGray: TextStyle,
    val bodyLargeBold: TextStyle,
    val bodyLargeGrayBold: TextStyle,
    val bodyLargeWhiteBold: TextStyle,
    val bodyMedium: TextStyle,
    val bodyMediumGray: TextStyle,
    val bodyMediumBold: TextStyle,
    val bodySmall: TextStyle,
    val bodySmallBold: TextStyle,
    val bodySmallGray: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle
)