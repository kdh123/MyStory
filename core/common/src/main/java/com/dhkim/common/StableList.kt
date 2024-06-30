package com.dhkim.common

import androidx.compose.runtime.Stable

@Stable
data class StableList<out T>(
    val data: List<T> = listOf()
)