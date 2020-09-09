package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.*
import java.text.SimpleDateFormat
import java.util.*

data class VEvent(
    val uid: String? = null,
    val stamp: String? = null,
    val organizer: String? = null,
    val description: String? = null,
    val location: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val summary: String? = null
) : Schema {

    companion object {
        private const val SCHEMA_PREFIX = "BEGIN:VEVENT"
        private const val SCHEMA_SUFFIX = "END:VEVENT"
        private const val PARAMETERS_SEPARATOR_1 = "\n"
        private const val PARAMETERS_SEPARATOR_2 = "\r"
        private const val UID_PREFIX = "UID:"
        private const val STAMP_PREFIX = "DTSTAMP:"
        private const val ORGANIZER_PREFIX = "ORGANIZER:"
        private const val DESCRIPTION_PREFIX = "DESCRIPTION:"
        private const val LOCATION_PREFIX = "LOCATION:"
        private const val START_PREFIX = "DTSTART:"
        private const val END_PREFIX = "DTEND:"
        private const val SUMMARY_PREFIX = "SUMMARY:"

        private val DATE_PARSERS by unsafeLazy {
            listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
                SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'"),
                SimpleDateFormat("yyyyMMdd'T'HHmmss"),
                SimpleDateFormat("yyyy-MM-dd"),
                SimpleDateFormat("yyyyMMdd")
            )
        }

        private val BARCODE_TEXT_DATE_FORMATTER by unsafeLazy {
            SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }

        private val FORMATTED_TEXT_DATE_FORMATTER by unsafeLazy {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        }

        fun parse(text: String): VEvent? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            var uid: String? = null
            var stamp: String? = null
            var organizer: String? = null
            var description: String? = null
            var location: String? = null
            var startDate: Long? = null
            var endDate: Long? = null
            var summary: String? = null

            text.removePrefixIgnoreCase(SCHEMA_PREFIX).split(PARAMETERS_SEPARATOR_1, PARAMETERS_SEPARATOR_2).forEach { part ->
                if (part.startsWithIgnoreCase(UID_PREFIX)) {
                    uid = part.removePrefixIgnoreCase(UID_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(STAMP_PREFIX)) {
                    stamp = part.removePrefixIgnoreCase(STAMP_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(ORGANIZER_PREFIX)) {
                    organizer = part.removePrefixIgnoreCase(ORGANIZER_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(DESCRIPTION_PREFIX)) {
                    description = part.removePrefixIgnoreCase(DESCRIPTION_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(LOCATION_PREFIX)) {
                    location = part.removePrefixIgnoreCase(LOCATION_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(START_PREFIX)) {
                    val startDateOriginal = part.removePrefix(START_PREFIX)
                    startDate = DATE_PARSERS.parseOrNull(startDateOriginal)?.time
                    return@forEach
                }

                if (part.startsWithIgnoreCase(END_PREFIX)) {
                    val endDateOriginal = part.removePrefix(END_PREFIX)
                    endDate = DATE_PARSERS.parseOrNull(endDateOriginal)?.time
                    return@forEach
                }

                if (part.startsWithIgnoreCase(SUMMARY_PREFIX)) {
                    summary = part.removePrefixIgnoreCase(SUMMARY_PREFIX)
                    return@forEach
                }
            }

            return VEvent(uid, stamp, organizer, description, location, startDate, endDate, summary)
        }
    }

    override val schema = BarcodeSchema.VEVENT

    override fun toFormattedText(): String {
        return listOf(
            uid,
            stamp,
            summary,
            description,
            location,
            FORMATTED_TEXT_DATE_FORMATTER.formatOrNull(startDate),
            FORMATTED_TEXT_DATE_FORMATTER.formatOrNull(endDate),
            organizer
        ).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        val startDate = BARCODE_TEXT_DATE_FORMATTER.formatOrNull(startDate)
        val endDate = BARCODE_TEXT_DATE_FORMATTER.formatOrNull(endDate)

        return StringBuilder()
            .append(SCHEMA_PREFIX)
            .append(PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(UID_PREFIX, uid, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(STAMP_PREFIX, stamp, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(ORGANIZER_PREFIX, organizer, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(DESCRIPTION_PREFIX, description, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(START_PREFIX, startDate, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(END_PREFIX, endDate, PARAMETERS_SEPARATOR_1)
            .appendIfNotNullOrBlank(SUMMARY_PREFIX, summary, PARAMETERS_SEPARATOR_1)
            .append(SCHEMA_SUFFIX)
            .toString()
    }
}