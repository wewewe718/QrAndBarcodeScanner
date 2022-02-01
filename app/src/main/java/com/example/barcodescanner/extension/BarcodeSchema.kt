package com.example.barcodescanner.extension

import com.example.barcodescanner.R
import com.example.barcodescanner.model.schema.BarcodeSchema

fun BarcodeSchema.toImageId(): Int? {
    return when (this) {
        BarcodeSchema.BOOKMARK -> R.drawable.ic_bookmark
        BarcodeSchema.CRYPTOCURRENCY -> R.drawable.ic_bitcoin
        BarcodeSchema.EMAIL -> R.drawable.ic_email
        BarcodeSchema.GEO -> R.drawable.ic_location
        BarcodeSchema.APP -> R.drawable.ic_app
        BarcodeSchema.MMS -> R.drawable.ic_mms
        BarcodeSchema.MECARD -> R.drawable.ic_contact
        BarcodeSchema.PHONE -> R.drawable.ic_phone
        BarcodeSchema.OTP_AUTH -> R.drawable.ic_otp
        BarcodeSchema.SMS -> R.drawable.ic_sms
        BarcodeSchema.URL -> R.drawable.ic_link
        BarcodeSchema.VEVENT -> R.drawable.ic_calendar
        BarcodeSchema.VCARD -> R.drawable.ic_contact
        BarcodeSchema.WIFI -> R.drawable.ic_wifi
        BarcodeSchema.YOUTUBE -> R.drawable.ic_youtube
        BarcodeSchema.BOARDINGPASS -> R.drawable.ic_boardingpass
        else -> null
    }
}

fun BarcodeSchema.toStringId(): Int? {
    return when (this) {
        BarcodeSchema.BOOKMARK -> R.string.barcode_schema_bookmark
        BarcodeSchema.CRYPTOCURRENCY -> R.string.barcode_schema_cryptocurrency
        BarcodeSchema.EMAIL -> R.string.barcode_schema_email
        BarcodeSchema.GEO -> R.string.barcode_schema_geo
        BarcodeSchema.APP -> R.string.barcode_schema_google_play
        BarcodeSchema.MMS -> R.string.barcode_schema_mms
        BarcodeSchema.MECARD -> R.string.barcode_schema_me_card
        BarcodeSchema.PHONE -> R.string.barcode_schema_phone
        BarcodeSchema.OTP_AUTH -> R.string.barcode_schema_otp
        BarcodeSchema.SMS -> R.string.barcode_schema_sms
        BarcodeSchema.URL -> R.string.barcode_schema_url
        BarcodeSchema.VEVENT -> R.string.barcode_schema_v_event
        BarcodeSchema.VCARD -> R.string.barcode_schema_v_card
        BarcodeSchema.WIFI -> R.string.barcode_schema_wifi
        BarcodeSchema.YOUTUBE -> R.string.barcode_schema_youtube
        BarcodeSchema.BOARDINGPASS -> R.string.barcode_schema_boardingpass
        BarcodeSchema.OTHER -> R.string.barcode_schema_other
        else -> null
    }
}