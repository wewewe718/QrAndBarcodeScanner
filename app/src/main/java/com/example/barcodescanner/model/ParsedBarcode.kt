package com.example.barcodescanner.model

import ezvcard.Ezvcard
import net.glxn.qrgen.core.scheme.MeCard

class ParsedBarcode(barcode: Barcode) {
    val id: Long = barcode.id
    val text = barcode.text
    val format = barcode.format
    val schema = barcode.schema
    val date = barcode.date
    val errorCorrectionLevel = barcode.errorCorrectionLevel

    var name: String? = null
    var organization: String? = null
    var jobTitle: String? = null
    var address: String? = null

    var email: String? = null
    var emailSubject: String? = null
    var emailBody: String? = null

    var emailType: String? = null
    var secondaryEmail: String? = null
    var secondaryEmailType: String? = null
    var tertiaryEmail: String? = null
    var tertiaryEmailType: String? = null

    var phone: String? = null
    var phoneType: String? = null
    var secondaryPhone: String? = null
    var secondaryPhoneType: String? = null
    var tertiaryPhone: String? = null
    var tertiaryPhoneType: String? = null

    var smsBody: String? = null

    var networkAuthType: String? = null
    var networkName: String? = null
    var networkPassword: String? = null

    var url: String? = null
    var googlePlayUrl: String? = null
    var youtubeUrl: String? = null
    var geoUri: String? = null


    init {
        when (schema) {
            BarcodeSchema.EMAIL -> parseEmail()
            BarcodeSchema.GEO_INFO -> parseGeoInfo()
            BarcodeSchema.GOOGLE_PLAY -> parseGooglePlay()
            BarcodeSchema.MMS,
            BarcodeSchema.SMS -> parseSms()
            BarcodeSchema.MECARD -> parseMeCard()
            BarcodeSchema.PHONE -> parsePhone()
            BarcodeSchema.VCARD -> parseVCard()
            BarcodeSchema.WIFI -> parseWifi()
            BarcodeSchema.YOUTUBE -> parseYoutube()
            BarcodeSchema.URL -> parseUrl()
        }
    }

    private fun parseEmail() {
        val parts = text.split(";")
        email = parts.getOrNull(0).orEmpty().replace("MATMSG:TO:", "")
        emailSubject = parts.getOrNull(1).orEmpty().replace("SUB:", "")
        emailBody = parts.getOrNull(2).orEmpty().replace("BODY:", "")
    }

    private fun parseGeoInfo() {
        geoUri = text
    }

    private fun parseGooglePlay() {
        googlePlayUrl = text
    }

    private fun parseSms() {
        val parts = text.split(":")
        phone = parts.getOrNull(1)
        smsBody = parts.getOrNull(2)
    }

    private fun parsePhone() {
        phone = text.replace("tel:", "")
    }

    private fun parseMeCard() {
        val meCard = MeCard.parse(text)
        name = meCard.name
        address = meCard.name
        phone = meCard.telephone
        email = meCard.email
    }

    private fun parseVCard() {
        val vCard = Ezvcard.parse(text).first()

        name = vCard.structuredName?.let { "${it.family} ${it.given}" }
        organization = vCard.organizations?.firstOrNull()?.values?.firstOrNull()
        jobTitle = vCard.titles?.firstOrNull()?.value
        url = vCard.urls?.firstOrNull()?.value
        geoUri = vCard.addresses?.firstOrNull()?.geo?.toString()

        vCard.emails?.getOrNull(0)?.apply {
            email = value
            emailType = types.getOrNull(0)?.value
        }
        vCard.emails?.getOrNull(1)?.apply {
            secondaryEmail = value
            secondaryEmailType = types.getOrNull(0)?.value
        }
        vCard.emails?.getOrNull(2)?.apply {
            tertiaryEmail = value
            tertiaryEmailType = types.getOrNull(0)?.value
        }

        vCard.telephoneNumbers?.getOrNull(0)?.apply {
            phone = text
            phoneType = types?.firstOrNull()?.value
        }
        vCard.telephoneNumbers?.getOrNull(1)?.apply {
            secondaryPhone = text
            secondaryPhoneType = types?.firstOrNull()?.value
        }
        vCard.telephoneNumbers?.getOrNull(2)?.apply {
            tertiaryPhone = text
            tertiaryPhoneType = types?.firstOrNull()?.value
        }
    }

    private fun parseWifi() {
        val parts = text.split(";")
        networkAuthType = parts.getOrNull(0).orEmpty().replace("WIFI:T:", "")
        networkName = parts.getOrNull(1).orEmpty().replace("S:", "")
        networkPassword = parts.getOrNull(2).orEmpty().replace("P:", "")
    }

    private fun parseYoutube() {
        youtubeUrl = text
    }

    private fun parseUrl() {
        url = text
    }
}