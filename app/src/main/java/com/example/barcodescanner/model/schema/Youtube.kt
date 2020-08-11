package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.startsWithAnyIgnoreCase

class Youtube(val url: String) : Schema {

    companion object {
        private val PREFIXES = listOf("vnd.youtube://", "http://www.youtube.com/watch?v=", "https://www.youtube.com/watch?v=")

        fun parse(text: String): Youtube? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return Youtube(text)
        }
    }

    override val schema = BarcodeSchema.YOUTUBE
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}