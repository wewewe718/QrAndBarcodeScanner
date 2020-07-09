package com.example.barcodescanner.extension

import android.widget.EditText

fun EditText.isEmpty(): Boolean {
    return text.isEmpty()
}

fun EditText.isNotEmpty(): Boolean {
    return text.isNotEmpty()
}

fun EditText.isNotBlank(): Boolean {
    return text.isNotBlank()
}

val EditText.textString: String
    get() = text.toString()
