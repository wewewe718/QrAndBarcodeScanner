package com.example.barcodescanner.usecase

import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.schema.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType

object BarcodeParser {

    fun parseResult(result: Result): Barcode {
        val schema = parseSchema(result.barcodeFormat, result.text)
        return Barcode(
            text = result.text,
            formattedText = schema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema.schema,
            date = result.timestamp,
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String,
            country = result.resultMetadata?.get(ResultMetadataType.POSSIBLE_COUNTRY) as? String
        )
    }

    fun parseSchema(format: BarcodeFormat, text: String): Schema {
        if (format != BarcodeFormat.QR_CODE) {
            return BoardingPass.parse(text)
                   ?:Other(text)
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
            ?: OtpAuth.parse(text)
            ?: NZCovidTracer.parse(text)
            ?: BoardingPass.parse(text)
            ?: Other(text)
    }
}