package com.example.barcodescanner.extension

import android.net.Uri

fun Uri.Builder.appendQueryParameterIfNotNull(key: String, value: String?): Uri.Builder {
    if (value != null) {
        appendQueryParameter(key, value)
    }
    return this
}