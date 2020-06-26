package com.example.barcodescanner.model.schema

enum class BarcodeSchema {
    BOOKMARK,
    EMAIL,
    GEO_INFO,
    GIROCODE,
    GOOGLE_PLAY,
    CALENDAR,
    MMS,
    MECARD,
    SMS,
    PHONE,
    VCARD,
    WIFI,
    YOUTUBE,
    URL,
    RECEIPT,
    OTHER;
}

interface Schema {
    val schema: BarcodeSchema
    fun toFormattedText(): String
    fun toBarcodeText(): String
}