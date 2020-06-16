package com.example.qrcodescanner.usecase

import android.graphics.Bitmap
import com.example.qrcodescanner.model.QrCode
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder

class BarcodeImageGenerator {
    private val encoder = BarcodeEncoder()

    fun generateImage(qrCode: QrCode): Bitmap {
        return encoder.encodeBitmap(qrCode.text, qrCode.format, 2000, 2000, mapOf(
            EncodeHintType.ERROR_CORRECTION to qrCode.errorCorrectionLevel,
            EncodeHintType.CHARACTER_SET to "utf-8",
            EncodeHintType.MARGIN to 0
        ))
    }
}