package com.example.barcodescanner.extension

import android.provider.ContactsContract
import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import org.apache.commons.codec.binary.Base32
import java.util.*

private val escapedRegex = """\\([\\;,":])""".toRegex()

fun String.unescape(): String {
    return replace(escapedRegex) { escaped ->
        escaped.groupValues[1]
    }
}

fun String?.toHmacAlgorithm(): HmacAlgorithm {
    return when (this) {
        "SHA1" -> HmacAlgorithm.SHA1
        "SHA256" -> HmacAlgorithm.SHA256
        "SHA512" -> HmacAlgorithm.SHA512
        else -> HmacAlgorithm.SHA1
    }
}

fun String?.decodeBase32(): ByteArray? {
    if (isNullOrBlank()) {
        return null
    }
    return Base32().decode(this)
}

fun String.toCaps(): String {
    return toUpperCase(Locale.ROOT)
}

fun String.removeStartAll(symbol: Char): String {
    var newStart = 0

    run loop@ {
        forEachIndexed { index, c ->
            if (c == symbol) {
                newStart = index + 1
            } else {
                return@loop
            }
        }
    }

    return if (newStart >= length) {
        ""
    } else {
        substring(newStart)
    }
}

fun String.removePrefixIgnoreCase(prefix: String): String {
    return substring(prefix.length)
}

fun String.startsWithIgnoreCase(prefix: String): Boolean {
    return startsWith(prefix, true)
}

fun String.startsWithAnyIgnoreCase(prefixes: List<String>): Boolean {
    prefixes.forEach { prefix ->
        if (startsWith(prefix, true)) {
            return true
        }
    }
    return false
}

fun String.equalsAnyIgnoreCase(others: List<String>): Boolean {
    others.forEach { other ->
        if (equals(other, true)) {
            return true
        }
    }
    return false
}

fun String.endsWithIgnoreCase(prefix: String): Boolean {
    return endsWith(prefix, true)
}

fun List<String?>.joinToStringNotNullOrBlankWithLineSeparator(): String {
    return joinToStringNotNullOrBlank("\n")
}

fun List<String?>.joinToStringNotNullOrBlank(separator: String): String {
    return filter { it.isNullOrBlank().not() }.joinToString(separator)
}

fun String.toCountryEmoji(): String {
    if (this.length != 2) {
        return ""
    }

    val countryCodeCaps = toUpperCase(Locale.US)
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

fun String.toEmailType(): Int? {
    return when (toUpperCase(Locale.US)) {
        "HOME" -> ContactsContract.CommonDataKinds.Email.TYPE_HOME
        "WORK" -> ContactsContract.CommonDataKinds.Email.TYPE_WORK
        "OTHER" -> ContactsContract.CommonDataKinds.Email.TYPE_OTHER
        "MOBILE" -> ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
        else -> null
    }
}

fun String.toPhoneType(): Int? {
   return when (toUpperCase(Locale.US)) {
       "HOME" -> ContactsContract.CommonDataKinds.Phone.TYPE_HOME
       "MOBILE", "CELL" -> ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
       "WORK" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK
       "FAX_WORK" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK
       "FAX_HOME" -> ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME
       "PAGER" -> ContactsContract.CommonDataKinds.Phone.TYPE_PAGER
       "OTHER" -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
       "CALLBACK" -> ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK
       "CAR" -> ContactsContract.CommonDataKinds.Phone.TYPE_CAR
       "COMPANY_MAIN" -> ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN
       "ISDN" -> ContactsContract.CommonDataKinds.Phone.TYPE_ISDN
       "MAIN" -> ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
       "OTHER_FAX" -> ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX
       "RADIO" -> ContactsContract.CommonDataKinds.Phone.TYPE_RADIO
       "TELEX" -> ContactsContract.CommonDataKinds.Phone.TYPE_TELEX
       "TTY_TDD" -> ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD
       "WORK_MOBILE" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE
       "WORK_PAGER" -> ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER
       "ASSISTANT" -> ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT
       "MMS" -> ContactsContract.CommonDataKinds.Phone.TYPE_MMS
       else -> null
   }
}

fun String.toAddressType(): Int? {
    return when (toUpperCase(Locale.US)) {
        "HOME" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME
        "WORK" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
        "OTHER" -> ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER
        else -> null
    }
}