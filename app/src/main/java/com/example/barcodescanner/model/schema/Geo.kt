package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

class Geo : Schema {

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

    private val uri: String

    private constructor(uri: String) {
        this.uri = uri
    }

    constructor(latitude: String, longitude: String, altitude: String? = null) {
        uri = if (altitude.isNullOrEmpty()) {
            "$PREFIX$latitude$SEPARATOR$longitude"
        } else {
            "$PREFIX$latitude$SEPARATOR$longitude$SEPARATOR$altitude"
        }
    }

    override val schema = BarcodeSchema.GEO

    override fun toBarcodeText(): String = uri

    override fun toFormattedText(): String {
        return uri.removePrefixIgnoreCase(PREFIX).replace(SEPARATOR, "\n")
    }
}