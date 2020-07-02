package com.example.barcodescanner.usecase

import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.schema.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType

object BarcodeScanResultParser {

    fun parseResult(result: Result): Barcode {
        val schema = getSchema(result)
        return Barcode(
            text = result.text,
            formattedText = schema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema.schema,
            date = result.timestamp,
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String
        )
    }

    private fun getSchema(result: Result): Schema {
        val text = result.text

        if (result.barcodeFormat != BarcodeFormat.QR_CODE) {
            return Other(text)
        }

        return GooglePlay.parse(text)
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
            ?: Receipt.parse(text)
            ?: Calendar.parse(text)
            ?: MeCard.parse(text)
            ?: VCard.parse(text)
            ?: Other(text)
    }
}