package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.startsWithAnyIgnoreCase

class GooglePlay(val url: String) : Schema {

    companion object {
        private val PREFIXES = listOf("market://details?id=", "{{{market://details?id=", "http://play.google.com/", "https://play.google.com/")

        fun parse(text: String): GooglePlay? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return GooglePlay(text)
        }
    }

    override val schema = BarcodeSchema.GOOGLE_PLAY
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}