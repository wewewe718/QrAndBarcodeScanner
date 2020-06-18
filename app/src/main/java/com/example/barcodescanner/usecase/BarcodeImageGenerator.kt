package com.example.barcodescanner.usecase

import android.graphics.Bitmap
import com.example.barcodescanner.model.ParsedBarcode
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder

class BarcodeImageGenerator {
    private val encoder = BarcodeEncoder()

    fun generateImage(barcode: ParsedBarcode, width: Int, height: Int, margin: Int = 0): Bitmap {
        return encoder.encodeBitmap(barcode.text, barcode.format, width, height, mapOf(
            EncodeHintType.ERROR_CORRECTION to barcode.errorCorrectionLevel,
            EncodeHintType.CHARACTER_SET to "utf-8",
            EncodeHintType.MARGIN to margin
        ))
    }
}