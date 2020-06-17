package com.example.barcodescanner.usecase

import com.example.barcodescanner.model.BarcodeSchema
import com.example.barcodescanner.model.Email
import com.example.barcodescanner.model.Sms

class BarcodeSchemaParser {

    private val prefixes = mapOf(
        BarcodeSchema.BOOKMARK to listOf("MEBKM:"),
        BarcodeSchema.EMAIL to listOf("mailto", "MATMSG"),
        BarcodeSchema.GEO_INFO to listOf("geo:", "http://maps.google.com/", "https://maps.google.com/"),
        BarcodeSchema.GIROCODE to listOf("BCD"),
        BarcodeSchema.GOOGLE_PLAY to listOf("market://details?id=", "{{{market://details?id=", "http://play.google.com/", "https://play.google.com/"),
        BarcodeSchema.ICALL to listOf("BEGIN:VCALENDAR", "BEGIN:VEVENT"),
        BarcodeSchema.MMS to listOf("mmsto:"),
        BarcodeSchema.MECARD to listOf("MECARD:"),
        BarcodeSchema.SMS to listOf("smsto:"),
        BarcodeSchema.PHONE to listOf("tel:"),
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

    fun parseAsPhone(text: String): String? {
        return text.split(":").getOrNull(1)
    }

    fun parseAsSms(text: String): Sms? {
        return try {
            val parts = text.split(":")
            Sms(
                phone = parts.getOrNull(1).orEmpty(),
                content = parts.getOrNull(2).orEmpty()
            )
        } catch (_: Exception) {
            null
        }
    }

    fun parseAsEmail(text: String): Email? {
        return when {
            text.startsWith("MATMSG:") -> parseAsMatmsgEmail(text)
            else -> null
        }
    }

    private fun parseAsMatmsgEmail(text: String): Email? {
        return try {
            val parts = text.split(";")
            val address = parts[0].replace("MATMSG:TO:", "")
            val subject = parts[1].replace("SUB:", "")
            val body = parts[2].replace("BODY:", "")
            Email(address, subject, body)
        } catch (ex: Exception) {
            null
        }
    }
}