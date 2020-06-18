package com.example.barcodescanner.usecase

import com.example.barcodescanner.model.BarcodeSchema
import com.example.barcodescanner.model.Email
import com.example.barcodescanner.model.Sms
import com.example.barcodescanner.model.Wifi
import ezvcard.Ezvcard
import ezvcard.VCard

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

    fun parseAsWifi(text: String): Wifi? {
        val parts = text.split(";")
        val authType = parts.getOrNull(0).orEmpty().replace("WIFI:T:", "")
        val name = parts.getOrNull(1).orEmpty().replace("S:", "")
        val password = parts.getOrNull(2).orEmpty().replace("P:", "")
        return Wifi(authType, name, password)
    }

    fun parseAsVCard(text: String): VCard? {
        return try{
            Ezvcard.parse(text).first()
        } catch (_: Exception) {
            null
        }
    }

    fun parseAsSms(text: String): Sms? {
        val parts = text.split(":")
        return Sms(
            phone = parts.getOrNull(1).orEmpty(),
            content = parts.getOrNull(2).orEmpty()
        )
    }

    fun parseAsEmail(text: String): Email? {
        return when {
            text.startsWith("MATMSG:") -> parseAsMatmsgEmail(text)
            else -> null
        }
    }

    private fun parseAsMatmsgEmail(text: String): Email? {
        val parts = text.split(";")
        val address = parts.getOrNull(0).orEmpty().replace("MATMSG:TO:", "")
        val subject = parts.getOrNull(1).orEmpty().replace("SUB:", "")
        val body = parts.getOrNull(2).orEmpty().replace("BODY:", "")
        return Email(address, subject, body)
    }
}