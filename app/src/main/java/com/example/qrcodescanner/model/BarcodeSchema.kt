package com.example.qrcodescanner.model

import net.glxn.qrgen.core.scheme.Schema

enum class BarcodeSchema {
    BOOKMARK,
    EMAIL,
    GEO_INFO,
    GIROCODE,
    GOOGLE_PLAY,
    ICALL,
    MMS,
    MECARD,
    SMS,
    TELEPHONE,
    VCARD,
    WIFI,
    YOUTUBE,
    URL,
    OTHER;

    companion object {
        fun from(text: String): BarcodeSchema {
            return BarcodeSchemaParser.parseSchema(text)
        }
    }
}

object BarcodeSchemaParser {

    private val prefixes = mapOf(
        BarcodeSchema.BOOKMARK to listOf("MEBKM:"),
        BarcodeSchema.EMAIL to listOf("mailto", "MATMSG"),
        BarcodeSchema.GEO_INFO to listOf("geo:", "http://maps.google.com/", "https://maps.google.com/"),
        BarcodeSchema.GIROCODE to listOf("BCD"),
        BarcodeSchema.GOOGLE_PLAY to listOf("market://details?id=", "{{{market://details?id=", "http://play.google.com/", "https://play.google.com/"),
        BarcodeSchema.ICALL to listOf("BEGIN:VCALENDAR", "BEGIN:VEVENT"),
        BarcodeSchema.MMS to listOf("mms:"),
        BarcodeSchema.MECARD to listOf("MECARD:"),
        BarcodeSchema.SMS to listOf("smsto:"),
        BarcodeSchema.TELEPHONE to listOf("tel:"),
        BarcodeSchema.VCARD to listOf("BEGIN:VCARD"),
        BarcodeSchema.WIFI to listOf("WIFI:"),
        BarcodeSchema.YOUTUBE to listOf("vnd.youtube://", "http://www.youtube.com/watch?v=", "https://www.youtube.com/watch?v="),
        BarcodeSchema.URL to listOf("http://", "https://")
    )

    fun parseSchema(text: String): BarcodeSchema {
        BarcodeSchema.values().forEach { schema ->
            prefixes[schema]?.forEach { prefix ->
                if (text.startsWith(prefix, true)) {
                    return schema
                }
            }
        }
        return BarcodeSchema.OTHER
    }

    fun parseAsSms(text: String): Pair<String?, String?>? {
        return try {
            val parts = text.split(":")
            Pair(parts[1], parts[2])
        } catch (_: Exception) {
            null
        }
    }
}