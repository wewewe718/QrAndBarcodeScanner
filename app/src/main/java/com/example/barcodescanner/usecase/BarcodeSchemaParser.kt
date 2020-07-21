package com.example.barcodescanner.usecase

import com.example.barcodescanner.model.schema.*
import com.google.zxing.BarcodeFormat

object BarcodeSchemaParser {

    fun getSchema(format: BarcodeFormat, text: String): Schema {
        if (format != BarcodeFormat.QR_CODE) {
            return Other(text)
        }

        return App.parse(text)
            ?: Youtube.parse(text)
            ?: GoogleMaps.parse(text)
            ?: Url.parse(text)
            ?: Phone.parse(text)
            ?: Geo.parse(text)
            ?: Bookmark.parse(text)
            ?: Sms.parse(text)
            ?: Mms.parse(text)
            ?: Wifi.parse(text)
            ?: Email.parse(text)
            ?: Cryptocurrency.parse(text)
            ?: VEvent.parse(text)
            ?: MeCard.parse(text)
            ?: VCard.parse(text)
            ?: Other(text)
    }
}