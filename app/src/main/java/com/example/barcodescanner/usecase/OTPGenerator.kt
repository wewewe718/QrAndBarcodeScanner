package com.example.barcodescanner.usecase

import com.example.barcodescanner.extension.decodeBase32
import com.example.barcodescanner.extension.toHmacAlgorithm
import com.example.barcodescanner.model.schema.OtpAuth
import dev.turingcomplete.kotlinonetimepassword.*
import java.util.concurrent.TimeUnit


object OTPGenerator {
    private const val DEFAULT_DIGITS = 6
    private const val HOTP_INITIAL_COUNTER = 0L
    private const val TOTP_DEFAULT_PERIOD = 30L

    fun generateOTP(otp: OtpAuth): String? {
        val secret = otp.secret.decodeBase32() ?: return null
        val algorithm = otp.algorithm.toHmacAlgorithm()
        val digits = otp.digits ?: DEFAULT_DIGITS
        val counter = otp.counter ?: HOTP_INITIAL_COUNTER
        val period = otp.period ?: TOTP_DEFAULT_PERIOD

        return when (otp.type) {
            OtpAuth.TOTP_TYPE -> generateTOTP(secret, period, digits, algorithm)
            OtpAuth.HOTP_TYPE -> generateHOTP(secret, counter, digits, algorithm)
            else -> null
        }
    }

    private fun generateTOTP(secret: ByteArray, period: Long, digits: Int, algorithm: HmacAlgorithm): String {
        val config = TimeBasedOneTimePasswordConfig(
            timeStep = period,
            timeStepUnit = TimeUnit.SECONDS,
            codeDigits = digits,
            hmacAlgorithm = algorithm
        )
        val generator = TimeBasedOneTimePasswordGenerator(secret, config)
        return generator.generate()
    }

    private fun generateHOTP(secret: ByteArray, counter: Long, digits: Int, algorithm: HmacAlgorithm): String {
        val config = HmacOneTimePasswordConfig(
            codeDigits = digits,
            hmacAlgorithm = algorithm
        )
        val generator = HmacOneTimePasswordGenerator(secret, config)
        return generator.generate(counter)
    }
}