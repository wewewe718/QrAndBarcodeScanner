package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.encodeBase32
import com.example.barcodescanner.extension.isNotBlank
import com.example.barcodescanner.extension.textString
import com.example.barcodescanner.extension.toHmacAlgorithm
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.model.schema.OtpAuth
import com.example.barcodescanner.model.schema.Schema
import dev.turingcomplete.kotlinonetimepassword.RandomSecretGenerator
import kotlinx.android.synthetic.main.fragment_create_qr_code_otp.*
import java.util.*

class CreateQrCodeOtpFragment : BaseCreateBarcodeFragment() {
    private val randomGenerator = RandomSecretGenerator()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOtpTypesSpinner()
        initAlgorithmsSpinner()
        initEditTexts()
        initGenerateRandomSecretButton()
        showRandomSecret()
    }

    override fun getBarcodeSchema(): Schema {
        return OtpAuth(
            type = spinner_opt_types.selectedItem?.toString()?.toLowerCase(Locale.ENGLISH),
            algorithm = spinner_algorithms.selectedItem?.toString(),
            label = if (edit_text_issuer.isNotBlank()) {
                "${edit_text_issuer.textString}:${edit_text_account.textString}"
            } else {
                edit_text_account.textString
            },
            issuer = edit_text_issuer.textString,
            digits = edit_text_digits.textString.toIntOrNull(),
            period = edit_text_period.textString.toLongOrNull(),
            counter = edit_text_counter.textString.toLongOrNull(),
            secret = edit_text_secret.textString
        )
    }

    private fun initOtpTypesSpinner() {
        spinner_opt_types.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.fragment_create_qr_code_otp_types, R.layout.item_spinner
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        spinner_opt_types.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                text_input_layout_counter.isVisible = position == 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun initAlgorithmsSpinner() {
        spinner_algorithms.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.fragment_create_qr_code_otp_algorithms, R.layout.item_spinner
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
    }

    private fun initEditTexts() {
        edit_text_account.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_secret.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_period.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_counter.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun initGenerateRandomSecretButton() {
        button_generate_random_secret.setOnClickListener {
            showRandomSecret()
        }
    }

    private fun toggleCreateBarcodeButton() {
        val isHotp = spinner_opt_types.selectedItemPosition == 0
        val areGeneralFieldsNotBlank = edit_text_account.isNotBlank() && edit_text_secret.isNotBlank()
        val areHotpFieldsNotBlank = edit_text_counter.isNotBlank() && edit_text_period.isNotBlank()
        parentActivity.isCreateBarcodeButtonEnabled = areGeneralFieldsNotBlank && (isHotp.not() || isHotp && areHotpFieldsNotBlank)
    }

    private fun showRandomSecret() {
        edit_text_secret.setText(generateRandomSecret())
    }

    private fun generateRandomSecret(): String {
        val algorithm = spinner_algorithms.selectedItem?.toString().toHmacAlgorithm()
        val secret = randomGenerator.createRandomSecret(algorithm)
        return secret.encodeBase32()
    }
}