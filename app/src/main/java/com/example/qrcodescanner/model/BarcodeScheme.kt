package com.example.qrcodescanner.model

import net.glxn.qrgen.core.scheme.*

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
    URL,
    VCARD,
    WIFI,
    YOUTUBE,
    OTHER;

    companion object {
        fun from(text: String): BarcodeSchema {
            return when {
                isScheme(text, Bookmark()) -> BOOKMARK
                isScheme(text, EMail()) || text.startsWith("MATMSG", true) -> EMAIL
                isScheme(text, GeoInfo()) -> GEO_INFO
                isScheme(text, Girocode()) -> GIROCODE
                isScheme(text, GooglePlay()) -> GOOGLE_PLAY
                text.startsWith("BEGIN:VCALENDAR") || text.startsWith("BEGIN:VEVENT") -> ICALL
                isScheme(text, MMS()) -> MMS
                isScheme(text, MeCard()) -> MECARD
                isScheme(text, SMS()) -> SMS
                isScheme(text, Telephone()) -> TELEPHONE
                isScheme(text, Url()) -> URL
                isScheme(text, VCard()) -> VCARD
                isScheme(text, Wifi()) -> WIFI
                isScheme(text, YouTube()) -> YOUTUBE
                else -> OTHER
            }
        }

        private fun isScheme(text: String, schema: Schema): Boolean {
            return try {
                schema.parseSchema(text)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}