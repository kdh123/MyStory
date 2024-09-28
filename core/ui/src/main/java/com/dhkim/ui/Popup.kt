package com.dhkim.ui

sealed interface Popup {

    val title: String
    val desc: String
    val positiveText: String
    val onPositiveClick: () -> Unit

    data class Warning(
        override val title: String,
        override val desc: String,
        override val positiveText: String = "확인",
        override val onPositiveClick: () -> Unit,
        val negativeText: String = "취소",
        val onNegativeClick: (() -> Unit)? = null
    ) : Popup

    data class OneButton(
        override val title: String,
        override val desc: String,
        override val positiveText: String,
        override val onPositiveClick: () -> Unit,
    ) : Popup
}