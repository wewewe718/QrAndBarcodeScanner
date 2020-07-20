package com.example.barcodescanner.extension

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

fun String.containsAll(others: List<String>): Boolean {
    others.forEach { other ->
        if (contains(other).not()) {
            return false
        }
    }
    return true
}

fun List<String?>.joinNotNullOrBlankToStringWithLineSeparator(): String {
    return filter { it.isNullOrBlank().not() }.joinToString("\n")
}

fun String.toCountryEmoji(): String {
    if (this.length != 2) {
        return ""
    }

    val countryCodeCaps = toUpperCase()
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}