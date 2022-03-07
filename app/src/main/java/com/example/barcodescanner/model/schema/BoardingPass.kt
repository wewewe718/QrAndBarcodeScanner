package com.example.barcodescanner.model.schema


import com.example.barcodescanner.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.barcodescanner.extension.startsWithIgnoreCase
import com.example.barcodescanner.extension.unsafeLazy
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class BoardingPass(
    val name: String? = null,
    val pnr: String? = null,
    val from: String? = null,
    val to: String? = null,
    val carrier: String? = null,
    val flight: String? = null,
    val date: String? = null,
    val dateJ: Int = 0,
    val cabin: String? = null,
    val seat: String? = null,
    val seq: String? = null,
    val ticket: String? = null,
    val selectee: String? = null,
    val ffAirline: String? = null,
    val ffNo: String? = null,
    val fasttrack: String? = null,
    val blob: String? = null,
) : Schema {

    companion object {
        private const val TAG = "QRandBAR"
        private val DATE_FORMATTER by unsafeLazy { SimpleDateFormat("d MMMM", Locale.ENGLISH) }

        fun parse(text: String): BoardingPass? {
            try {

                if (text.length < 60) {
                    return null
                }

                // M1 means single leg barcode
                if (text.startsWithIgnoreCase("M1").not()) {
                    return null
                }
                // E means electronic ticket
                if (text[22] != 'E') {
                    return null
                }
                val fieldSize: Int = text.slice(58..59).toInt(16)
                // > is the marker for the airline specific optional fields
                if (fieldSize != 0 && text[60] != '>') {
                    return null
                }
                // ^ is the security marker; sometimes missing on paper passes
                if (text.length > 60 + fieldSize && text[60+fieldSize] != '^') {
                    return null
                }

                val name = text.slice(2..21).trim()
                val pnr = text.slice(23..29).trim()
                val from = text.slice(30..32)
                val to = text.slice(33..35)
                val carrier = text.slice(36..38).trim()
                val flight = text.slice(39..43).trim()
                val dateJ = text.slice(44..46).toInt()
                val cabin = text.slice(47..47)
                val seat = text.slice(48..51).trim()
                val seq = text.slice(52..56)
                // 57 is status - ignore

                val today = Calendar.getInstance()
                today.set(Calendar.DAY_OF_YEAR, dateJ)
                val date: String = DATE_FORMATTER.format(today.getTime())
                var selectee : String? = null
                var ticket : String? = null
                var ffAirline : String? = null
                var ffNo : String? = null
                var fasttrack: String? = null

                if (fieldSize != 0) {
                    // don't actually use version but it must parse as an Int
                    @Suppress("UNUSED_VARIABLE")
                    val version: Int = text.slice(61..61).toInt()
                    val size: Int = text.slice(62..63).toInt(16)

                    if (size != 0 && size < 11) {
                        return null
                    }
                    // don't really care about the first optional field
                    // it's mostly baggage and checkin information
                    val size1: Int = text.slice(64+size..65+size).toInt(16)
                    // European boarding passes are 42 to have appended fasttrack
                    // US boarding passes are size 41 with no fasttrack
                    if (size1 != 0 && (size1 < 37 || size1 > 42)) {
                        return null
                    } else {
                        ticket = text.slice(66+size..78+size).trim()
                        // TSA field:
                        // blank for not US flights
                        // 0 - normal cleared
                        // 1 - no fly
                        // 2 - selected for enhanced security
                        // 3 - precheck
                        selectee = text.slice(79+size..79+size)
                        ffAirline = text.slice(84+size..86+size).trim()
                        ffNo = text.slice(87+size..102+size).trim()
                        if (size1 == 42) {
                            // Y - fasttrack eligible
                            fasttrack = text.slice(107+size..107+size)
                        }
                    }
                }

                return BoardingPass(name, pnr, from, to, carrier, flight, date,
                                    dateJ, cabin, seat, seq, ticket, selectee,
                                    ffAirline, ffNo, fasttrack,
                                    text)
            } catch(e: Exception) {
                // mostly number format and parse past end of string errors
                return null
            }
        }
    }

    override val schema = BarcodeSchema.BOARDINGPASS
    override fun toFormattedText(): String = listOf(name, pnr, "$from->$to", "$carrier$flight", date, cabin, seat, seq, ticket, selectee, "$ffAirline$ffNo", fasttrack).joinToStringNotNullOrBlankWithLineSeparator()
    override fun toBarcodeText(): String {
             return blob ?: ""
    }
}