package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.isNotBlank
import com.example.barcodescanner.extension.textString
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.model.schema.Bookmark
import com.example.barcodescanner.model.schema.Schema
import kotlinx.android.synthetic.main.fragment_create_qr_code_bookmark.*

class CreateQrCodeBookmarkFragment : BaseCreateBarcodeFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitleEditText()
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
        return Bookmark(
            title = edit_text_title.textString,
            url = edit_text_url.textString
        )
    }

    private fun initTitleEditText() {
        edit_text_title.requestFocus()
    }

    private fun handleTextChanged() {
        edit_text_title.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_url.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled = edit_text_title.isNotBlank() || edit_text_url.isNotBlank()
    }
}