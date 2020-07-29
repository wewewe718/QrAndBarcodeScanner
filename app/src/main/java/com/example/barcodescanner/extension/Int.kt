package com.example.barcodescanner.extension

fun Int?.orZero(): Int {
    return this ?: 0
}