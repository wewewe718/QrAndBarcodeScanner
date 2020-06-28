package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.*
import java.text.SimpleDateFormat
import java.util.*

data class MeCard(
    val firstName: String? = null,
    val lastName: String? = null,
    val nickname: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val birthday: String? = null,
    val note: String? = null,
    val address: String? = null
) : Schema {

    companion object {
        private const val SCHEMA_PREFIX = "MECARD:"
        private const val NAME_PREFIX = "N:"
        private const val NICKNAME_PREFIX = "NICK:"
        private const val PHONE_PREFIX = "TEL:"
        private const val EMAIL_PREFIX = "EMAIL:"
        private const val BIRTHDAY_PREFIX = "BDAY:"
        private const val NOTE_PREFIX = "TEL:"
        private const val ADDRESS_PREFIX = "ADR:"
        private const val NAME_SEPARATOR = ","
        private const val PARAMETER_SEPARATOR = ";"
        private const val SCHEMA_SUFFIX = ";;"
        private val DATE_PARSER by lazy { SimpleDateFormat("yyyyMMdd", Locale.ENGLISH) }
        private val DATE_FORMATTER by lazy { SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH) }

        fun parse(text: String): MeCard? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            var firstName: String? = null
            var lastName: String? = null
            var nickname: String? = null
            var phone: String? = null
            var email: String? = null
            var birthday: String? = null
            var note: String? = null
            var address: String? = null

            text.removePrefixIgnoreCase(SCHEMA_PREFIX).split(PARAMETER_SEPARATOR).forEach { keyValue ->
                if (keyValue.startsWithIgnoreCase(NAME_PREFIX)) {
                    val lastAndFirstNames = keyValue.removePrefixIgnoreCase(NAME_PREFIX).split(NAME_SEPARATOR)
                    lastName = lastAndFirstNames.getOrNull(0)
                    firstName = lastAndFirstNames.getOrNull(1)
                    return@forEach
                }

                if (keyValue.startsWithIgnoreCase(NICKNAME_PREFIX)) {
                    nickname = keyValue.removePrefixIgnoreCase(NICKNAME_PREFIX)
                    return@forEach
                }

                if (keyValue.startsWithIgnoreCase(PHONE_PREFIX)) {
                    phone = keyValue.removePrefixIgnoreCase(PHONE_PREFIX)
                    return@forEach
                }

                if (keyValue.startsWithIgnoreCase(EMAIL_PREFIX)) {
                    email = keyValue.removePrefixIgnoreCase(EMAIL_PREFIX)
                    return@forEach
                }

                if (keyValue.startsWithIgnoreCase(BIRTHDAY_PREFIX)) {
                    birthday = keyValue.removePrefixIgnoreCase(BIRTHDAY_PREFIX)
                    return@forEach
                }

                if (keyValue.startsWithIgnoreCase(NOTE_PREFIX)) {
                    note = keyValue.removePrefixIgnoreCase(NOTE_PREFIX)
                    return@forEach
                }

                if (keyValue.startsWithIgnoreCase(ADDRESS_PREFIX)) {
                    address = keyValue.removePrefixIgnoreCase(ADDRESS_PREFIX)
                    return@forEach
                }
            }

            return MeCard(firstName, lastName, nickname, phone, email, birthday, note, address)
        }
    }

    override val schema = BarcodeSchema.MECARD

    override fun toFormattedText(): String {
        val parsedBirthday = DATE_PARSER.parseOrNull(birthday)?.time
        val formattedBirthday = DATE_FORMATTER.formatOrNull(parsedBirthday)

        return listOf(
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            nickname,
            formattedBirthday,
            phone,
            email,
            birthday,
            note,
            address
        ).joinNotNullToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return SCHEMA_PREFIX +
                "$NAME_PREFIX${firstName.orEmpty()}$NAME_SEPARATOR${lastName.orEmpty()}$PARAMETER_SEPARATOR" +
                "$NICKNAME_PREFIX${nickname.orEmpty()}$PARAMETER_SEPARATOR" +
                "$PHONE_PREFIX${phone.orEmpty()}$PARAMETER_SEPARATOR" +
                "$EMAIL_PREFIX${email.orEmpty()}$PARAMETER_SEPARATOR" +
                "$BIRTHDAY_PREFIX${birthday.orEmpty()}$PARAMETER_SEPARATOR" +
                "$NOTE_PREFIX${note.orEmpty()}$PARAMETER_SEPARATOR" +
                "$ADDRESS_PREFIX${address.orEmpty()}$PARAMETER_SEPARATOR" +
                SCHEMA_SUFFIX
    }
}