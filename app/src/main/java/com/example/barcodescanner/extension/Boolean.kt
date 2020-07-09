package com.example.barcodescanner.extension

fun Boolean?.orTrue(): Boolean {
    return this ?: true
}

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}