package com.example.barcodescanner.model.schema

enum class BarcodeSchema {
    BOOKMARK,
    CRYPTOCURRENCY,
    EMAIL,
    GEO,
    GOOGLE_MAPS,
    GOOGLE_PLAY,
    MMS,
    MECARD,
    PHONE,
    RECEIPT,
    SMS,
    URL,
    VEVENT,
    VCARD,
    WIFI,
    YOUTUBE,
    OTHER;
}

interface Schema {
    val schema: BarcodeSchema
    fun toFormattedText(): String
    fun toBarcodeText(): String
}