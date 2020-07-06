package com.example.barcodescanner.usecase

import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.schema.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType

class BarcodeScanResultParser(private val barcodeSchemaParser: BarcodeSchemaParser) {

    fun parseResult(result: Result): Barcode {
        val schema = barcodeSchemaParser.getSchema(result.barcodeFormat, result.text)
        return Barcode(
            text = result.text,
            formattedText = schema.toFormattedText(),
            format = result.barcodeFormat,
            schema = schema.schema,
            date = result.timestamp,
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String
        )
    }
}