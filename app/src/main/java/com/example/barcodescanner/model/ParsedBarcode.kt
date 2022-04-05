package com.example.barcodescanner.model

import com.example.barcodescanner.model.schema.*
import com.google.zxing.BarcodeFormat

class ParsedBarcode(barcode: Barcode) {
    var id = barcode.id
    var name = barcode.name
    val text = barcode.text
    val formattedText = barcode.formattedText
    val format = barcode.format
    val schema = barcode.schema
    val date = barcode.date
    var isFavorite = barcode.isFavorite
    val country = barcode.country

    var firstName: String? = null
    var lastName: String? = null
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
    var note: String? = null

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
    var isHidden: Boolean? = null
    var anonymousIdentity: String? = null
    var identity: String? = null
    var eapMethod: String? = null
    var phase2Method: String? = null

    var bookmarkTitle: String? = null
    var url: String? = null
    var youtubeUrl: String? = null
    var bitcoinUri: String? = null
    var otpUrl: String? = null
    var geoUri: String? = null

    var eventUid: String? = null
    var eventStamp: String? = null
    var eventOrganizer: String? = null
    var eventDescription: String? = null
    var eventLocation: String? = null
    var eventSummary: String? = null
    var eventStartDate: Long? = null
    var eventEndDate: Long? = null

    var appMarketUrl: String? = null
    var appPackage: String? = null

    val isInDb: Boolean
        get() = id != 0L

    val isProductBarcode: Boolean
        get() = when (format) {
            BarcodeFormat.EAN_8, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.UPC_E -> true
            else -> false
        }

    init {
        when (schema) {
            BarcodeSchema.BOOKMARK -> parseBookmark()
            BarcodeSchema.EMAIL -> parseEmail()
            BarcodeSchema.GEO,
            BarcodeSchema.GOOGLE_MAPS -> parseGeoInfo()
            BarcodeSchema.APP -> parseApp()
            BarcodeSchema.VEVENT -> parseCalendar()
            BarcodeSchema.MMS,
            BarcodeSchema.SMS -> parseSms()
            BarcodeSchema.MECARD -> parseMeCard()
            BarcodeSchema.PHONE -> parsePhone()
            BarcodeSchema.VCARD -> parseVCard()
            BarcodeSchema.WIFI -> parseWifi()
            BarcodeSchema.YOUTUBE -> parseYoutube()
            BarcodeSchema.CRYPTOCURRENCY -> parseBitcoin()
            BarcodeSchema.OTP_AUTH -> parseOtp()
            BarcodeSchema.NZCOVIDTRACER -> parseNZCovidTracer()
            BarcodeSchema.BOARDINGPASS,
            BarcodeSchema.URL -> parseUrl()
            else -> {}
        }
    }

    private fun parseBookmark() {
        val bookmark = Bookmark.parse(text) ?: return
        bookmarkTitle = bookmark.title
        url = bookmark.url
    }

    private fun parseEmail() {
        val email = Email.parse(text) ?: return
        this.email = email.email
        emailSubject = email.subject
        emailBody = email.body
    }

    private fun parseGeoInfo() {
        geoUri = text
    }

    private fun parseApp() {
        appMarketUrl = text
        appPackage = App.parse(text)?.appPackage
    }

    private fun parseCalendar() {
        val calendar = VEvent.parse(text) ?: return
        eventUid = calendar.uid
        eventStamp = calendar.stamp
        eventOrganizer = calendar.organizer
        eventDescription = calendar.description
        eventLocation = calendar.location
        eventSummary = calendar.summary
        eventStartDate = calendar.startDate
        eventEndDate = calendar.endDate
    }

    private fun parseSms() {
        val sms = Sms.parse(text) ?: return
        phone = sms.phone
        smsBody = sms.message
    }

    private fun parsePhone() {
        phone = Phone.parse(text)?.phone
    }

    private fun parseMeCard() {
        val meCard = MeCard.parse(text) ?: return
        firstName = meCard.firstName
        lastName = meCard.lastName
        address = meCard.address
        phone = meCard.phone
        email = meCard.email
        note = meCard.note
    }

    private fun parseVCard() {
        val vCard = VCard.parse(text) ?: return
        
        firstName = vCard.firstName
        lastName = vCard.lastName
        organization = vCard.organization
        jobTitle = vCard.title
        url = vCard.url
        geoUri = vCard.geoUri
        
        phone = vCard.phone
        phoneType = vCard.phoneType
        secondaryPhone = vCard.secondaryPhone
        secondaryPhoneType = vCard.secondaryPhoneType
        tertiaryPhone = vCard.tertiaryPhone
        tertiaryPhoneType = vCard.tertiaryPhoneType

        email = vCard.email
        emailType = vCard.emailType
        secondaryEmail = vCard.secondaryEmail
        secondaryEmailType = vCard.secondaryEmailType
        tertiaryEmail = vCard.tertiaryEmail
        tertiaryEmailType = vCard.tertiaryEmailType
    }

    private fun parseWifi() {
        val wifi = Wifi.parse(text) ?: return
        networkAuthType = wifi.encryption
        networkName = wifi.name
        networkPassword = wifi.password
        isHidden = wifi.isHidden
        anonymousIdentity = wifi.anonymousIdentity
        identity = wifi.identity
        eapMethod = wifi.eapMethod
        phase2Method = wifi.phase2Method
    }

    private fun parseYoutube() {
        youtubeUrl = text
    }

    private fun parseBitcoin() {
        bitcoinUri = text
    }

    private fun parseOtp() {
        otpUrl = text
    }

    private fun parseUrl() {
        url = text
    }

    private fun parseNZCovidTracer() {
        val objNZCovidTracer = NZCovidTracer.parse(text) ?: return
        //title = objNZCovidTracer.title
        address = objNZCovidTracer.addr
        url = "http://maps.google.com/maps?q=" + (objNZCovidTracer.addr)?.replace("\n", ", ")
    }
}