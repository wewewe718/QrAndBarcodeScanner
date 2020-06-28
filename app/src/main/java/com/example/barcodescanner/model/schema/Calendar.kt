package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.*
import java.text.SimpleDateFormat
import java.util.*

data class Calendar(
    val uid: String? = null,
    val stamp: String? = null,
    val organizer: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val summary: String? = null
) : Schema {

    private companion object {
        private const val SCHEMA_PREFIX = "BEGIN:VEVENT"
        private const val SCHEMA_SUFFIX = "END:VEVENT"
        private const val PARAMETERS_SEPARATOR = "\r?\n"
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

        fun parse(text: String): Calendar? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            var uid: String? = null
            var stamp: String? = null
            var organizer: String? = null
            var startDate: String? = null
            var endDate: String? = null
            var summary: String? = null

            text.removePrefixIgnoreCase(SCHEMA_PREFIX).split(PARAMETERS_SEPARATOR).forEach { part ->
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
                    startDate = part.removePrefix(START_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(END_PREFIX)) {
                    endDate = part.removePrefix(START_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(SUMMARY_PREFIX)) {
                    summary = part.removePrefixIgnoreCase(SUMMARY_PREFIX)
                    return@forEach
                }
            }

            return Calendar(uid, stamp, organizer, startDate, endDate, summary)
        }
    }

    override val schema = BarcodeSchema.CALENDAR

    override fun toFormattedText(): String {
        val parsedStartDate = DATE_PARSER.parseOrNull(startDate)?.time
        val formattedStartDate = DATE_FORMATTER.formatOrNull(parsedStartDate)

        val parsedEndDate = DATE_PARSER.parseOrNull(startDate)?.time
        val formattedEndDate = DATE_FORMATTER.formatOrNull(parsedEndDate)

        return listOf(
            uid,
            stamp,
            formattedStartDate,
            formattedEndDate,
            organizer,
            summary
        ).joinNotNullToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return "$SCHEMA_PREFIX$PARAMETERS_SEPARATOR" +
                "$UID_PREFIX${uid.orEmpty()}$PARAMETERS_SEPARATOR" +
                "$STAMP_PREFIX${stamp.orEmpty()}$PARAMETERS_SEPARATOR" +
                "$ORGANIZER_PREFIX${organizer.orEmpty()}$PARAMETERS_SEPARATOR" +
                "$START_PREFIX${startDate.orEmpty()}$PARAMETERS_SEPARATOR" +
                "$END_PREFIX${endDate.orEmpty()}$PARAMETERS_SEPARATOR" +
                "$SUMMARY_PREFIX${summary.orEmpty()}$PARAMETERS_SEPARATOR" +
                "$SCHEMA_SUFFIX$PARAMETERS_SEPARATOR"
    }
}