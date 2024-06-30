package com.dhkim.common


fun String.profileImage(): Int {
    return when (this) {
        "0" -> R.drawable.ic_smile_blue
        "1" -> R.drawable.ic_smile_violet
        "2" -> R.drawable.ic_smile_green
        "3" -> R.drawable.ic_smile_orange
        else -> R.drawable.ic_smile_red
    }
}

fun Int.profileImage(): String {
    return when (this) {
        R.drawable.ic_smile_blue -> "0"
        R.drawable.ic_smile_violet -> "1"
        R.drawable.ic_smile_green -> "2"
        R.drawable.ic_smile_orange -> "3"
        else -> "0"
    }
}