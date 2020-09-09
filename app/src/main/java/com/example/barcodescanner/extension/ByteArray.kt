package com.example.barcodescanner.extension

import org.apache.commons.codec.binary.Base32

fun ByteArray.encodeBase32(): String {
    return Base32().encodeAsString(this)
}