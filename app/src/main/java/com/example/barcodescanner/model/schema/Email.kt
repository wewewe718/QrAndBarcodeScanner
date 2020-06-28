package com.example.barcodescanner.model.schema

import android.net.MailTo
import com.example.barcodescanner.extension.joinNotNullToStringWithLineSeparator
import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

data class Email(
    val email: String? = null,
    val subject: String? = null,
    val body: String? = null
) : Schema {

    companion object {
        private const val MATMSG_SCHEMA_PREFIX = "MATMSG:"
        private const val MATMSG_EMAIL_PREFIX = "TO:"
        private const val MATMSG_SUBJECT_PREFIX = "SUB:"
        private const val MATMSG_BODY_PREFIX = "BODY:"
        private const val MATMSG_SEPARATOR = ";"

        private const val MAILTO_SCHEMA_PREFIX = "mailto:"

        fun parse(text: String): Email? {
            return when {
                text.startsWithIgnoreCase(MATMSG_SCHEMA_PREFIX) -> parseAsMatmsg(text)
                text.startsWithIgnoreCase(MAILTO_SCHEMA_PREFIX) -> parseAsMailTo(text)
                else -> null
            }
        }

        private fun parseAsMatmsg(text: String): Email {
            var email: String? = null
            var subject: String? = null
            var body: String? = null

            text.removePrefixIgnoreCase(MATMSG_SCHEMA_PREFIX).split(MATMSG_SEPARATOR).forEach { part ->
                if (part.startsWithIgnoreCase(MATMSG_EMAIL_PREFIX)) {
                    email = part.removePrefixIgnoreCase(MATMSG_EMAIL_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(MATMSG_SUBJECT_PREFIX)) {
                    subject = part.removePrefixIgnoreCase(MATMSG_SUBJECT_PREFIX)
                    return@forEach
                }

                if (part.startsWithIgnoreCase(MATMSG_BODY_PREFIX)) {
                    body = part.removePrefixIgnoreCase(MATMSG_BODY_PREFIX)
                    return@forEach
                }
            }

            return Email(email, subject, body)
        }

        private fun parseAsMailTo(text: String): Email? {
            return try {
                val mailto = MailTo.parse(text)
                Email(mailto.to, mailto.subject, mailto.body)
            } catch (_: Exception) {
                null
            }
        }
    }

    override val schema = BarcodeSchema.EMAIL

    override fun toFormattedText(): String {
        return listOf(email, subject, body).joinNotNullToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return MATMSG_SCHEMA_PREFIX +
                "$MATMSG_EMAIL_PREFIX${email.orEmpty()}$MATMSG_SEPARATOR" +
                "$MATMSG_SUBJECT_PREFIX${subject.orEmpty()}$MATMSG_SEPARATOR" +
                "$MATMSG_BODY_PREFIX${body.orEmpty()}$MATMSG_SEPARATOR"
    }
}