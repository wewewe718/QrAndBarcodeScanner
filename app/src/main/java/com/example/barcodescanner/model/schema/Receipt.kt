package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.containsAll
import com.example.barcodescanner.extension.formatOrNull
import com.example.barcodescanner.extension.orZero
import com.example.barcodescanner.extension.parseOrNull
import java.text.SimpleDateFormat
import java.util.*

class Receipt(
    val type: Int? = null,
    val time: String? = null,
    val fiscalDriveNumber: String? = null,
    val fiscalDocumentNumber: String? = null,
    val fiscalSign: String? = null,
    val sum: String? = null
) : Schema {

    companion object {
        private const val TYPE_PREFIX = "n="
        private const val TIME_PREFIX = "t="
        private const val FISCAL_DRIVE_NUMBER_PREFIX = "fn="
        private const val FISCAL_DOCUMENT_NUMBER_PREFIX = "i="
        private const val FISCAL_SIGN_PREFIX = "fp="
        private const val SUM_PREFIX = "s="
        private const val SEPARATOR = "&"
        private val PREFIXES = listOf(TYPE_PREFIX, TIME_PREFIX, FISCAL_DRIVE_NUMBER_PREFIX, FISCAL_DOCUMENT_NUMBER_PREFIX, FISCAL_SIGN_PREFIX, SUM_PREFIX)
        private val DATE_PARSER by lazy { SimpleDateFormat("yyyyMMdd'T'HHmm", Locale.US) }
        private val DATE_FORMATTER by lazy { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH) }

        fun parse(text: String): Receipt? {
            if (text.containsAll(PREFIXES).not()) {
                return null
            }

            var type: Int? = null
            var time: String? = null
            var fiscalDriveNumber: String? = null
            var fiscalDocumentNumber: String? = null
            var fiscalSign: String? = null
            var sum: String? = null

            text.split(SEPARATOR).forEach { part ->
                if (part.startsWith(TYPE_PREFIX)) {
                    type = part.removePrefix(TYPE_PREFIX).toIntOrNull()
                    return@forEach
                }

                if (part.startsWith(TIME_PREFIX)) {
                    time = part.removePrefix(TIME_PREFIX)
                    return@forEach
                }

                if (part.startsWith(FISCAL_DRIVE_NUMBER_PREFIX)) {
                    fiscalDriveNumber = part.removePrefix(FISCAL_DRIVE_NUMBER_PREFIX)
                    return@forEach
                }

                if (part.startsWith(FISCAL_DOCUMENT_NUMBER_PREFIX)) {
                    fiscalDocumentNumber = part.removePrefix(FISCAL_DOCUMENT_NUMBER_PREFIX)
                    return@forEach
                }

                if (part.startsWith(FISCAL_SIGN_PREFIX)) {
                    fiscalSign = part.removePrefix(FISCAL_SIGN_PREFIX)
                    return@forEach
                }

                if (part.startsWith(SEPARATOR)) {
                    sum = part.removePrefix("s=")
                    return@forEach
                }
            }

            return Receipt(type, time, fiscalDriveNumber, fiscalDocumentNumber, fiscalSign, sum)
        }
    }

    override val schema = BarcodeSchema.RECEIPT

    override fun toFormattedText(): String {
        val type = when (type) {
            1 -> "Приход"
            2 -> "Возврат прихода"
            3 -> "Расход"
            4 -> "Возврат расхода"
            else -> "Приход"
        }

        val parsedTime = DATE_PARSER.parseOrNull(time)?.time
        val formattedTime = DATE_FORMATTER.formatOrNull(parsedTime)

        return String.format(
            "%s\n%s\nФН: %s\nФД: %s\nФПД: %s\nИтог: %s",
            formattedTime,
            type,
            fiscalDriveNumber.orEmpty(),
            fiscalDocumentNumber.orEmpty(),
            fiscalSign.orEmpty(),
            sum.orEmpty()
        )
    }

    override fun toBarcodeText(): String {
        return "$TYPE_PREFIX${type.orZero()}$SEPARATOR" +
                "$TIME_PREFIX${time.orEmpty()}$SEPARATOR" +
                "$FISCAL_DRIVE_NUMBER_PREFIX${fiscalDriveNumber.orEmpty()}$SEPARATOR" +
                "$FISCAL_DOCUMENT_NUMBER_PREFIX${fiscalDocumentNumber.orEmpty()}$SEPARATOR" +
                "$FISCAL_SIGN_PREFIX${fiscalSign.orEmpty()}$SEPARATOR" +
                "$SUM_PREFIX${sum.orEmpty()}"
    }
}