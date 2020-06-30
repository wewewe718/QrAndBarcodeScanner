package com.example.barcodescanner.extension

import com.example.barcodescanner.R
import com.google.zxing.BarcodeFormat

fun BarcodeFormat.toStringId(): Int {
    return when (this) {
        BarcodeFormat.AZTEC -> R.string.barcode_format_aztec
        BarcodeFormat.CODABAR -> R.string.barcode_format_codabar
        BarcodeFormat.CODE_39 -> R.string.barcode_format_code_39
        BarcodeFormat.CODE_93 -> R.string.barcode_format_code_93
        BarcodeFormat.CODE_128 -> R.string.barcode_format_code_128
        BarcodeFormat.DATA_MATRIX -> R.string.barcode_format_data_matrix
        BarcodeFormat.EAN_8 -> R.string.barcode_format_ean_8
        BarcodeFormat.EAN_13 -> R.string.barcode_format_ean_13
        BarcodeFormat.ITF -> R.string.barcode_format_itf_14
        BarcodeFormat.MAXICODE -> R.string.barcode_format_maxi_code
        BarcodeFormat.PDF_417 -> R.string.barcode_format_pdf_417
        BarcodeFormat.QR_CODE -> R.string.barcode_format_qr_code
        BarcodeFormat.RSS_14 -> R.string.barcode_format_rss_14
        BarcodeFormat.RSS_EXPANDED -> R.string.barcode_format_rss_expanded
        BarcodeFormat.UPC_A -> R.string.barcode_format_upc_a
        BarcodeFormat.UPC_E -> R.string.barcode_format_upc_e
        BarcodeFormat.UPC_EAN_EXTENSION -> R.string.barcode_format_upc_ean
    }
}