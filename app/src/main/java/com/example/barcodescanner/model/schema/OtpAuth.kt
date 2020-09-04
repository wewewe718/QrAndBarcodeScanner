package com.example.barcodescanner.model.schema

import android.net.Uri
import com.example.barcodescanner.extension.appendQueryParameterIfNotNullOrBlank
import java.io.Serializable

data class OtpAuth(
    val type: String? = null,
    val label: String? = null,
    val issuer: String? = null,
    val secret: String? = null,
    val algorithm: String? = null,
    val digits: Int? = null,
    val period: Long? = null,
    val counter: Long? = null
) : Schema, Serializable {

    companion object {
        const val TOTP_TYPE = "totp"
        const val HOTP_TYPE = "hotp"

        private const val URI_SCHEME = "otpauth"
        private const val SECRET_KEY = "secret"
        private const val ISSUER_KEY = "issuer"
        private const val ALGORITHM_KEY = "algorithm"
        private const val DIGITS_KEY = "digits"
        private const val COUNTER_KEY = "counter"
        private const val PERIOD_KEY = "period"

        fun parse(text: String): OtpAuth? {
            val uri = Uri.parse(text)

            if (uri.scheme != URI_SCHEME) {
                return null
            }

            val type = uri.authority
            if (type != HOTP_TYPE && type != TOTP_TYPE) {
                return null
            }

            var label = uri.path?.trim()
            if (label?.startsWith('/') == true) {
                label = label.substring(1)
            }

            val issuer = uri.getQueryParameter(ISSUER_KEY)
            val secret = uri.getQueryParameter(SECRET_KEY)
            val algorithm = uri.getQueryParameter(ALGORITHM_KEY)
            val digits = uri.getQueryParameter(DIGITS_KEY)?.toIntOrNull()
            val period = uri.getQueryParameter(PERIOD_KEY)?.toLongOrNull()
            val counter = uri.getQueryParameter(COUNTER_KEY)?.toLongOrNull()

            return OtpAuth(type, label, issuer, secret, algorithm, digits, period, counter)
        }
    }

    override val schema = BarcodeSchema.OTP_AUTH

    override fun toFormattedText(): String {
        return label.orEmpty()
    }

    override fun toBarcodeText(): String {
        return Uri.Builder()
            .scheme(URI_SCHEME)
            .authority(type)
            .appendPath(label)
            .appendQueryParameterIfNotNullOrBlank(SECRET_KEY, secret)
            .appendQueryParameterIfNotNullOrBlank(ISSUER_KEY, issuer)
            .appendQueryParameterIfNotNullOrBlank(ALGORITHM_KEY, algorithm)
            .appendQueryParameterIfNotNullOrBlank(DIGITS_KEY, digits?.toString())
            .appendQueryParameterIfNotNullOrBlank(COUNTER_KEY, counter?.toString())
            .appendQueryParameterIfNotNullOrBlank(PERIOD_KEY, period?.toString())
            .build()
            .toString()
    }

}