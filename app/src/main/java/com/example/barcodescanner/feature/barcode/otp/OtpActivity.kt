package com.example.barcodescanner.feature.barcode.otp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.otpGenerator
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.extension.orZero
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.model.schema.OtpAuth
import kotlinx.android.synthetic.main.activity_barcode_otp.*

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

    private lateinit var otp: OtpAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_otp)
        supportEdgeToEdge()
        parseOtp()
        handleToolbarBackClicked()
        handleRefreshOtpClicked()
        showOtp()
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun parseOtp() {
        otp = intent?.getSerializableExtra(OTP_KEY) as OtpAuth
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleRefreshOtpClicked() {
        button_refresh.setOnClickListener {
            refreshOtp()
        }
    }

    private fun refreshOtp() {
        otp = otp.copy(counter = otp.counter.orZero() + 1L)
        showOtp()
    }

    private fun showOtp() {
        button_refresh.isVisible = otp.type == OtpAuth.HOTP_TYPE
        text_view_counter.isVisible = otp.type == OtpAuth.HOTP_TYPE
        text_view_counter.text = getString(R.string.activity_barcode_otp_counter, otp.counter.orZero().toString())
        text_view_password.text = otpGenerator.generateOTP(otp) ?: getString(R.string.activity_barcode_otp_unable_to_generate_otp)
    }
}