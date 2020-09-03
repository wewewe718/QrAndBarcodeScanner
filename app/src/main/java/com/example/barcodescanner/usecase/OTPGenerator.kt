package com.example.barcodescanner.usecase

import android.R.attr.key
import com.example.barcodescanner.model.schema.OtpAuth
import org.apache.commons.codec.binary.Base32
import java.nio.ByteBuffer
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.math.pow


object OTPGenerator {
    private const val DEFAULT_DIGITS = 6
    private const val DEFAULT_ALGORITHM = "SHA1"
    private const val HOTP_INITIAL_COUNTER = 1L
    private const val TOTP_DEFAULT_PERIOD = 30

    fun generateOTP(otp: OtpAuth): Int? {
        val secret = decodeSecret(otp.secret) ?: return null
        val algorithm = otp.algorithm ?: DEFAULT_ALGORITHM
        val digits = otp.digits ?: DEFAULT_DIGITS
        val counter = otp.counter ?: HOTP_INITIAL_COUNTER
        val period = otp.period ?: TOTP_DEFAULT_PERIOD

        return when (otp.type) {
            "totp" -> generateTOTP(secret, period, digits, algorithm)
            "hotp" -> generateHOTP(secret, counter, digits, algorithm)
            else -> null
        }
    }

    private fun generateTOTP(secret: ByteArray, period: Int, digits: Int, algorithm: String): Int {
        val full = generateFullTOTP(secret, period, algorithm)
        val div = 10.0f.pow(digits).toInt()
        return full % div
    }

    private fun generateHOTP(secret: ByteArray, counter: Long, digits: Int, algorithm: String): Int {
        val full = generateFullHOTP(secret, counter, algorithm)
        val div = 10.0f.pow(digits).toInt()
        return full % div
    }

    private fun generateFullTOTP(secret: ByteArray, period: Int, algorithm: String): Int {
        val currentTimeInSeconds = System.currentTimeMillis() / 1000
        val counter = currentTimeInSeconds / period
        return generateFullHOTP(secret, counter, algorithm)
    }

    private fun generateFullHOTP(secret: ByteArray, counter: Long, algorithm: String): Int {
        var r = 0

        try {
            val data = ByteBuffer.allocate(8).putLong(counter).array()
            val hash = generateHash(algorithm, secret, data)
            val offset = hash[hash.size - 1] and 0xF
            var binary = (hash[offset] and 0x7F) shl 0x18
            binary = binary or (hash[offset + 1] and 0xFF.toByte() shl 0x10.toByte())
            binary = binary or (hash[offset + 2] and 0xFF.toByte() shl 0x08.toByte())
            binary = binary or (hash[offset + 3] and 0xFF.toByte())
            r = binary
        } catch (e: Exception) {
            Logger.log(e)
        }

        return r
    }

    private fun generateHash(algorithm: String, secret: ByteArray, data: ByteArray): ByteArray {
        val algo = "Hmac$algorithm"
        return Mac.getInstance(algo).run {
            init(SecretKeySpec(secret, algo))
            doFinal(data)
        }
    }

    private fun decodeSecret(secret: String?): ByteArray? {
        if (secret.isNullOrBlank()) {
            return null
        }
        return Base32().decode(secret.toUpperCase(Locale.ENGLISH))
    }
}