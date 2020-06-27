package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

data class Geo(val uri: String) : Schema {

    companion object {
        private const val PREFIX = "geo:"
        private const val SEPARATOR = ","

        fun parse(text: String): Geo? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }
            return Geo(text)
        }
    }

    constructor(latitude: Double, longitude: Double) : this("$PREFIX$latitude$SEPARATOR$longitude")

    override val schema = BarcodeSchema.GEO

    override fun toBarcodeText(): String = uri

    override fun toFormattedText(): String {
        return uri.removePrefixIgnoreCase(PREFIX).replace(SEPARATOR, "$SEPARATOR ")
    }
}