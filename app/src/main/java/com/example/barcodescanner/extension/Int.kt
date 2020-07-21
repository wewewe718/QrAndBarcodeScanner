package com.example.barcodescanner.extension

fun Int?.orZero(): Int {
    return this ?: 0
}

fun Int?.orNegative(): Int {
    return this ?: -1
}