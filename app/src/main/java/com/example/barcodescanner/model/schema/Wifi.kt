package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.joinNotNullOrBlankToStringWithLineSeparator
import com.example.barcodescanner.extension.orFalse
import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

class Wifi(
    val encryption: String? = null,
    val name: String? = null,
    val password: String? = null,
    val isHidden: Boolean? = null
) : Schema {

    companion object {
        private const val SCHEMA_PREFIX = "WIFI:"
        private const val ENCRYPTION_PREFIX = "T:"
        private const val NAME_PREFIX = "S:"
        private const val PASSWORD_PREFIX = "P:"
        private const val IS_HIDDEN_PREFIX = "H:"
        private const val SEPARATOR = ";"

        fun parse(text: String): Wifi? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            var encryption: String? = null
            var name: String? = null
            var password: String? = null
            var isHidden: Boolean? = null

            text.removePrefixIgnoreCase(SCHEMA_PREFIX)
                .split(SEPARATOR)
                .forEach { part ->
                    if (part.startsWithIgnoreCase(ENCRYPTION_PREFIX)) {
                        encryption = part.removePrefixIgnoreCase(ENCRYPTION_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(NAME_PREFIX)) {
                        name = part.removePrefixIgnoreCase(NAME_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(PASSWORD_PREFIX)) {
                        password = part.removePrefixIgnoreCase(PASSWORD_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(IS_HIDDEN_PREFIX)) {
                        isHidden = part.removePrefixIgnoreCase(IS_HIDDEN_PREFIX).toBoolean()
                        return@forEach
                    }
                }

            return Wifi(encryption, name, password, isHidden)
        }
    }

    override val schema = BarcodeSchema.WIFI

    override fun toFormattedText(): String {
        return listOf(name, encryption, password).joinNotNullOrBlankToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return SCHEMA_PREFIX +
                "$ENCRYPTION_PREFIX${encryption.orEmpty()}$SEPARATOR" +
                "$NAME_PREFIX${name.orEmpty()}$SEPARATOR" +
                "$PASSWORD_PREFIX${password.orEmpty()}$SEPARATOR" +
                "$IS_HIDDEN_PREFIX${isHidden.orFalse()}$SEPARATOR"
    }
}