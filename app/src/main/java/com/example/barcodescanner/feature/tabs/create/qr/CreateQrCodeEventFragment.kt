package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.makeSmoothScrollable
import com.example.barcodescanner.extension.textString
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.model.schema.Other
import com.example.barcodescanner.model.schema.Schema
import com.example.barcodescanner.model.schema.VEvent
import kotlinx.android.synthetic.main.fragment_create_qr_code_vevent.*

class CreateQrCodeEventFragment : BaseCreateBarcodeFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_vevent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScrollView()
        initTitleEditText()
        initCreateBarcodeButton()
    }

    override fun getBarcodeSchema(): Schema {
        return VEvent(
            uid = edit_text_title.textString,
            summary = edit_text_summary.textString,
            startDate = button_date_time_start.dateTime,
            endDate = button_date_time_end.dateTime
        )
    }

    private fun initScrollView() {
        scroll_view.makeSmoothScrollable()
    }

    private fun initTitleEditText() {
        edit_text_title.requestFocus()
    }

    private fun initCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled = true
    }
}