package com.example.barcodescanner.feature.common

import androidx.appcompat.app.AppCompatActivity
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.error.ErrorDialogFragment
import com.example.barcodescanner.model.BarcodeSchema
import com.google.zxing.BarcodeFormat

fun Int?.orZero(): Int {
    return this ?: 0
}

fun Long?.orZero(): Long {
    return this ?: 0
}

fun BarcodeFormat.toStringId(): Int {
    return when (this) {
        BarcodeFormat.AZTEC -> R.string.activity_barcode_format_aztec
        BarcodeFormat.CODABAR -> R.string.activity_barcode_format_codabar
        BarcodeFormat.CODE_39 -> R.string.activity_barcode_format_code_39
        BarcodeFormat.CODE_93 -> R.string.activity_barcode_format_code_93
        BarcodeFormat.CODE_128 -> R.string.activity_barcode_format_code_128
        BarcodeFormat.DATA_MATRIX -> R.string.activity_barcode_format_data_matrix
        BarcodeFormat.EAN_8 -> R.string.activity_barcode_format_ean_8
        BarcodeFormat.EAN_13 -> R.string.activity_barcode_format_ean_13
        BarcodeFormat.ITF -> R.string.activity_barcode_format_itf
        BarcodeFormat.MAXICODE -> R.string.activity_barcode_format_maxi_code
        BarcodeFormat.PDF_417 -> R.string.activity_barcode_format_pdf_417
        BarcodeFormat.QR_CODE -> R.string.activity_barcode_format_qr_code
        BarcodeFormat.RSS_14 -> R.string.activity_barcode_format_rss_14
        BarcodeFormat.RSS_EXPANDED -> R.string.activity_barcode_format_rss_expanded
        BarcodeFormat.UPC_A -> R.string.activity_barcode_format_upc_a
        BarcodeFormat.UPC_E -> R.string.activity_barcode_format_upc_e
        BarcodeFormat.UPC_EAN_EXTENSION -> R.string.activity_barcode_format_upc_ean
    }
}

fun BarcodeSchema.toImageId(): Int {
    return when (this) {
        BarcodeSchema.BOOKMARK -> R.drawable.ic_bookmark
        BarcodeSchema.EMAIL -> R.drawable.ic_email
        BarcodeSchema.GEO_INFO -> R.drawable.ic_location
        BarcodeSchema.GIROCODE -> R.drawable.ic_payment
        BarcodeSchema.GOOGLE_PLAY -> R.drawable.ic_app
        BarcodeSchema.CALENDAR -> R.drawable.ic_calendar
        BarcodeSchema.MMS -> R.drawable.ic_mms
        BarcodeSchema.MECARD -> R.drawable.ic_contact
        BarcodeSchema.SMS -> R.drawable.ic_sms
        BarcodeSchema.PHONE -> R.drawable.ic_phone
        BarcodeSchema.URL -> R.drawable.ic_link
        BarcodeSchema.VCARD -> R.drawable.ic_contact
        BarcodeSchema.WIFI -> R.drawable.ic_wifi
        BarcodeSchema.YOUTUBE -> R.drawable.ic_youtube
        BarcodeSchema.RECEIPT -> R.drawable.ic_receipt
        else -> R.drawable.ic_barcode_other
    }
}

fun AppCompatActivity.showError(error: Throwable) {
    val errorDialog = ErrorDialogFragment.newInstance(this, error)
    errorDialog.show(supportFragmentManager, "")
}