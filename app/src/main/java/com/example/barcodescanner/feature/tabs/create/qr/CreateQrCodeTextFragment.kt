package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.feature.tabs.create.CreateBarcodeActivity
import kotlinx.android.synthetic.main.fragment_create_qr_code_text.*

class CreateQrCodeTextFragment : BaseCreateBarcodeFragment() {
    private val parentActivity by lazy { requireActivity() as CreateBarcodeActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        handleTextChanged()
    }

    override fun getBarcodeText(): String {
        return edit_text.text?.toString().orEmpty()
    }

    private fun initEditText() {
        edit_text.requestFocus()
    }

    private fun handleTextChanged() {
        edit_text.addTextChangedListener {
            if (edit_text.text.isEmpty()) {
                parentActivity.disableCreateBarcodeButton()
            } else {
                parentActivity.enableCreateBarcodeButton()
            }
        }
    }
}