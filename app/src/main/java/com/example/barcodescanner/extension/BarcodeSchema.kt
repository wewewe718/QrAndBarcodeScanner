package com.example.barcodescanner.extension

import com.example.barcodescanner.R
import com.example.barcodescanner.model.schema.BarcodeSchema

fun BarcodeSchema.toImageId(): Int {
    return when (this) {
        BarcodeSchema.BOOKMARK -> R.drawable.ic_bookmark
        BarcodeSchema.EMAIL -> R.drawable.ic_email
        BarcodeSchema.GEO -> R.drawable.ic_location
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