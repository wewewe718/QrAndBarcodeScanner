package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.joinNotNullOrBlankToStringWithLineSeparator
import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase


data class Bookmark(
    val title: String? = null,
    val url: String? = null
) : Schema {

    companion object {
        private const val SCHEMA_PREFIX = "MEBKM:"
        private const val TITLE_PREFIX = "TITLE:"
        private const val URL_PREFIX = "URL:"
        private const val SEPARATOR = ";"

        fun parse(text: String): Bookmark? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            var title: String? = null
            var url: String? = null

            text.removePrefixIgnoreCase(SCHEMA_PREFIX)
                .split(SEPARATOR)
                .forEach { part ->
                    if (part.startsWithIgnoreCase(TITLE_PREFIX)) {
                        title = part.removePrefixIgnoreCase(TITLE_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(URL_PREFIX)) {
                        url = part.removePrefixIgnoreCase(URL_PREFIX)
                        return@forEach
                    }
                }

            return Bookmark(title, url)
        }
    }

    override val schema = BarcodeSchema.BOOKMARK

    override fun toFormattedText(): String {
        return listOf(title, url).joinNotNullOrBlankToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return SCHEMA_PREFIX +
                "$TITLE_PREFIX${title.orEmpty()}$SEPARATOR" +
                "$URL_PREFIX${url.orEmpty()}$SEPARATOR"
    }
}