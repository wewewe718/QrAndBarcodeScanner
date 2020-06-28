package com.example.barcodescanner.extension

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}