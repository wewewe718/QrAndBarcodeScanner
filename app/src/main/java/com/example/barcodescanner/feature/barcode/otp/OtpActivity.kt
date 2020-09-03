package com.example.barcodescanner.feature.barcode.otp

import android.content.Context
import android.content.Intent
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.model.schema.OtpAuth

class OtpActivity : BaseActivity() {

    companion object {
        private const val OTP_KEY = "OTP_KEY"

        fun start(context: Context, opt: OtpAuth) {
            val intent = Intent(context, OtpActivity::class.java).apply {
                putExtra(OTP_KEY, opt)
            }
            context.startActivity(intent)
        }
    }
}