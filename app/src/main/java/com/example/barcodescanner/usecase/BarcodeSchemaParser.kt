package com.example.barcodescanner.usecase

import com.example.barcodescanner.feature.common.containsAll
import com.example.barcodescanner.model.BarcodeSchema
import com.google.zxing.BarcodeFormat

class BarcodeSchemaParser {

    private val prefixes = mapOf(
        BarcodeSchema.BOOKMARK to listOf("MEBKM:"),
        BarcodeSchema.EMAIL to listOf("mailto", "MATMSG"),
        BarcodeSchema.GEO_INFO to listOf("geo:", "http://maps.google.com/", "https://maps.google.com/"),
        BarcodeSchema.GIROCODE to listOf("BCD"),
        BarcodeSchema.GOOGLE_PLAY to listOf("market://details?id=", "{{{market://details?id=", "http://play.google.com/", "https://play.google.com/"),
        BarcodeSchema.CALENDAR to listOf("BEGIN:VCALENDAR", "BEGIN:VEVENT"),
        BarcodeSchema.MMS to listOf("mmsto:"),
        BarcodeSchema.MECARD to listOf("MECARD:"),
        BarcodeSchema.SMS to listOf("smsto:"),
        BarcodeSchema.PHONE to listOf("tel:"),
        BarcodeSchema.VCARD to listOf("BEGIN:VCARD"),
        BarcodeSchema.WIFI to listOf("WIFI:"),
        BarcodeSchema.YOUTUBE to listOf("vnd.youtube://", "http://www.youtube.com/watch?v=", "https://www.youtube.com/watch?v="),
        BarcodeSchema.URL to listOf("http://", "https://"),
        BarcodeSchema.RECEIPT to listOf("t=", "s=", "fn=", "i=", "fp=", "n=")
    )

    fun parseSchema(text: String): BarcodeSchema {
        BarcodeSchema.values()
            .filter { schema ->
                schema != BarcodeSchema.RECEIPT
            }
            .forEach { schema ->
                prefixes[schema]?.forEach { prefix ->
                    if (text.startsWith(prefix, true)) {
                        return schema
                    }
                }
            }

        prefixes[BarcodeSchema.RECEIPT]?.also { receiptPrefixes ->
            if (text.containsAll(receiptPrefixes)) {
                return BarcodeSchema.RECEIPT
            }
        }

        return BarcodeSchema.OTHER
    }
}