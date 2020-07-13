package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.*
import java.text.SimpleDateFormat
import java.util.*

data class VEvent(
    val uid: String? = null,
    val stamp: String? = null,
    val organizer: String? = null,
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
        private const val START_PREFIX = "DTSTART:"
        private const val END_PREFIX = "DTEND:"
        private const val SUMMARY_PREFIX = "SUMMARY:"

        private val DATE_PARSER by lazy {
            SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }

        private val DATE_FORMATTER by lazy {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        }

        fun parse(text: String): VEvent? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            var uid: String? = null
            var stamp: String? = null
            var organizer: String? = null
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

                if (part.startsWithIgnoreCase(START_PREFIX)) {
                    val startDateOriginal = part.removePrefix(START_PREFIX)
                    startDate = DATE_PARSER.parseOrNull(startDateOriginal)?.time
                    return@forEach
                }

                if (part.startsWithIgnoreCase(END_PREFIX)) {
                    val endDateOriginal = part.removePrefix(END_PREFIX)
                    endDate = DATE_PARSER.parseOrNull(endDateOriginal)?.time
                    return@forEach
                }

                if (part.startsWithIgnoreCase(SUMMARY_PREFIX)) {
                    summary = part.removePrefixIgnoreCase(SUMMARY_PREFIX)
                    return@forEach
                }
            }

            return VEvent(uid, stamp, organizer, startDate, endDate, summary)
        }
    }

    override val schema = BarcodeSchema.VEVENT

    override fun toFormattedText(): String {
        return listOf(
            uid,
            stamp,
            summary,
            DATE_FORMATTER.formatOrNull(startDate),
            DATE_FORMATTER.formatOrNull(endDate),
            organizer
        ).joinNotNullOrBlankToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        val startDate = DATE_PARSER.formatOrNull(startDate)
        val endDate = DATE_PARSER.formatOrNull(endDate)

        return "$SCHEMA_PREFIX$PARAMETERS_SEPARATOR_1" +
                "$UID_PREFIX${uid.orEmpty()}$PARAMETERS_SEPARATOR_1" +
                "$STAMP_PREFIX${stamp.orEmpty()}$PARAMETERS_SEPARATOR_1" +
                "$ORGANIZER_PREFIX${organizer.orEmpty()}$PARAMETERS_SEPARATOR_1" +
                "$START_PREFIX${startDate.orEmpty()}$PARAMETERS_SEPARATOR_1" +
                "$END_PREFIX${endDate.orEmpty()}$PARAMETERS_SEPARATOR_1" +
                "$SUMMARY_PREFIX${summary.orEmpty()}$PARAMETERS_SEPARATOR_1" +
                "$SCHEMA_SUFFIX$PARAMETERS_SEPARATOR_1"
    }
}