package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeParser
import com.example.barcodescanner.extension.isNotBlank
import com.example.barcodescanner.extension.textString
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.model.schema.Schema
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_create_qr_code_text.*

class CreateQrCodeTextFragment : BaseCreateBarcodeFragment() {

    companion object {
        private const val DEFAULT_TEXT_KEY = "DEFAULT_TEXT_KEY"

        fun newInstance(defaultText: String): CreateQrCodeTextFragment {
            return CreateQrCodeTextFragment().apply {
                arguments = Bundle().apply {
                    putString(DEFAULT_TEXT_KEY, defaultText)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTextChanged()
        initEditText()
    }

    override fun getBarcodeSchema(): Schema {
        return barcodeParser.parseSchema(BarcodeFormat.QR_CODE, edit_text.textString)
    }

    private fun initEditText() {
        val defaultText = arguments?.getString(DEFAULT_TEXT_KEY).orEmpty()
        edit_text.apply {
            setText(defaultText)
            setSelection(defaultText.length)
            requestFocus()
        }
    }

    private fun handleTextChanged() {
        edit_text.addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled = edit_text.isNotBlank()
        }
    }
}