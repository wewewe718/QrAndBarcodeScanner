package com.example.barcodescanner.model

import ezvcard.Ezvcard
import net.glxn.qrgen.core.scheme.*
import java.text.SimpleDateFormat
import java.util.*

class ParsedBarcode(barcode: Barcode) {
    val id = barcode.id
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

    var eventUid: String? = null
    var eventStamp: String? = null
    var eventOrganizer: String? = null
    var eventStartDate: Long? = null
    var eventEndDate: Long? = null
    var eventSummary: String? = null

    private val calendarDateParser by lazy {
        SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }


    init {
        when (schema) {
            BarcodeSchema.BOOKMARK -> parseBookmark()
            BarcodeSchema.EMAIL -> parseEmail()
            BarcodeSchema.GEO_INFO -> parseGeoInfo()
            BarcodeSchema.GOOGLE_PLAY -> parseGooglePlay()
            BarcodeSchema.CALENDAR -> parseCalendar()
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

    private fun parseBookmark() {
        val parts = text.removePrefix("MEBKM:").split(";")
        parts.forEach { part ->
            if (part.startsWith("URL:")) {
                url = part.removePrefix("URL:")
                return
            }
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

    private fun parseCalendar() {
        val vEvent = when {
            text.startsWith("BEGIN:VCALENDAR") -> ICal.parse(text).subSchema as? IEvent?
            text.startsWith("BEGIN:VEVENT") -> IEvent.parse(SchemeUtil.getParameters(text), text)
            else -> null
        }

        vEvent?.apply {
            eventUid = uid
            eventStamp = stamp
            eventOrganizer = organizer
            eventSummary = summary
        }

        try {
            eventStartDate = calendarDateParser.parse(vEvent?.start).time
        } catch (_: Exception) {
        }

        try {
            eventEndDate = calendarDateParser.parse(vEvent?.end).time
        } catch (_: Exception) {
        }
    }

    private fun parseSms() {
        val parts = text.split(":")
        phone = parts.getOrNull(1)
        smsBody = parts.getOrNull(2)
    }

    private fun parsePhone() {
        phone = text.removePrefix("tel:")
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
        Wifi.parse(text).apply {
            networkAuthType = authentication
            networkName = ssid
            networkPassword = psk
        }
    }

    private fun parseYoutube() {
        youtubeUrl = text
    }

    private fun parseUrl() {
        url = text
    }
}