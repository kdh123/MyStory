package com.dhkim.ui

sealed interface Popup {

    val title: String
    val desc: String
    val positiveText: String
    val onPositiveClick: () -> Unit

    data class Warning(
        override val title: String,
        override val desc: String,
        override val positiveText: String,
        override val onPositiveClick: () -> Unit,
        val negativeText: String,
        val onNegativeClick: () -> Unit
    ) : Popup

    data class OneButton(
        override val title: String,
        override val desc: String,
        override val positiveText: String,
        override val onPositiveClick: () -> Unit,
    ) : Popup
}