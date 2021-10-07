package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.startsWithAnyIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

class Url(val url: String) : Schema {

    companion object {
        private const val HTTP_PREFIX = "http://"
        private const val HTTPS_PREFIX = "https://"
        private const val WWW_PREFIX = "www."
        private const val F_DROID_REPOSITORY_PREFIX = "fdroidrepos://"
        private val PREFIXES = listOf(HTTP_PREFIX, HTTPS_PREFIX, WWW_PREFIX, F_DROID_REPOSITORY_PREFIX)

        fun parse(text: String): Url? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }

            val url = when {
                text.startsWithIgnoreCase(WWW_PREFIX) -> "$HTTP_PREFIX$text"
                else -> text
            }

            return Url(url)
        }
    }

    override val schema = BarcodeSchema.URL
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}