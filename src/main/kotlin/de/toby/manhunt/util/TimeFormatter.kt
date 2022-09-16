package de.toby.manhunt.util

fun Int.formatToMinutes(): String {
    val minutes = this / 60
    val seconds = this % 60

    return "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
}