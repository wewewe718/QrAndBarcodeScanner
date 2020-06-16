package com.example.barcodescanner.usecase

import android.graphics.Bitmap
import com.example.barcodescanner.model.Barcode
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder

class BarcodeImageGenerator {
    private val encoder = BarcodeEncoder()

    fun generateImage(barcode: Barcode): Bitmap {
        return encoder.encodeBitmap(barcode.text, barcode.format, 2000, 2000, mapOf(
            EncodeHintType.ERROR_CORRECTION to barcode.errorCorrectionLevel,
            EncodeHintType.CHARACTER_SET to "utf-8",
            EncodeHintType.MARGIN to 0
        ))
    }
}