package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.appendIfNotNullOrBlank
import com.example.barcodescanner.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

class Wifi(
    val encryption: String? = null,
    val name: String? = null,
    val password: String? = null,
    val isHidden: Boolean? = null,
    val anonymousIdentity: String? = null,
    val identity: String? = null,
    val eapMethod: String? = null,
    val phase2Method: String? = null
) : Schema {

    companion object {
        private const val SCHEMA_PREFIX = "WIFI:"
        private const val ENCRYPTION_PREFIX = "T:"
        private const val NAME_PREFIX = "S:"
        private const val PASSWORD_PREFIX = "P:"
        private const val IS_HIDDEN_PREFIX = "H:"
        private const val ANONYMOUS_IDENTITY_PREFIX = "AI:"
        private const val IDENTITY_PREFIX = "I:"
        private const val EAP_PREFIX = "E:"
        private const val PHASE2_PREFIX = "PH2:"
        private const val SEPARATOR = ";"

        fun parse(text: String): Wifi? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            var encryption: String? = null
            var name: String? = null
            var password: String? = null
            var isHidden: Boolean? = null
            var anonymousIdentity: String? = null
            var identity: String? = null
            var eapMethod: String? = null
            var phase2Method: String? = null

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

                    if (part.startsWithIgnoreCase(ANONYMOUS_IDENTITY_PREFIX)) {
                        anonymousIdentity = part.removePrefixIgnoreCase(ANONYMOUS_IDENTITY_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(IDENTITY_PREFIX)) {
                        identity = part.removePrefixIgnoreCase(IDENTITY_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(EAP_PREFIX)) {
                        eapMethod = part.removePrefixIgnoreCase(EAP_PREFIX)
                        return@forEach
                    }

                    if (part.startsWithIgnoreCase(PHASE2_PREFIX)) {
                        phase2Method = part.removePrefixIgnoreCase(PHASE2_PREFIX)
                        return@forEach
                    }
                }

            return Wifi(
                    encryption,
                    name,
                    password,
                    isHidden,
                    anonymousIdentity,
                    identity,
                    eapMethod,
                    phase2Method
            )
        }
    }

    override val schema = BarcodeSchema.WIFI

    override fun toFormattedText(): String {
        return listOf(name, password).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return StringBuilder()
            .append(SCHEMA_PREFIX)
            .appendIfNotNullOrBlank(ENCRYPTION_PREFIX, encryption, SEPARATOR)
            .appendIfNotNullOrBlank(NAME_PREFIX, name, SEPARATOR)
            .appendIfNotNullOrBlank(PASSWORD_PREFIX, password, SEPARATOR)
            .appendIfNotNullOrBlank(IS_HIDDEN_PREFIX, isHidden?.toString(), SEPARATOR)
            .append(SEPARATOR)
            .toString()
    }
}