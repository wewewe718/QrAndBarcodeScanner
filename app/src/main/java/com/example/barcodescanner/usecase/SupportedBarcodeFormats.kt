package com.example.barcodescanner.usecase

import com.google.zxing.BarcodeFormat

object SupportedBarcodeFormats {
    val FORMATS = listOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.AZTEC,
        BarcodeFormat.PDF_417,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_A,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODABAR,
        BarcodeFormat.ITF
    )
}