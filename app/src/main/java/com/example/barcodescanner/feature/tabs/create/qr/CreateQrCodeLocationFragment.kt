package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.isNotBlank
import com.example.barcodescanner.extension.makeSmoothScrollable
import com.example.barcodescanner.extension.textString
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.model.schema.Geo
import com.example.barcodescanner.model.schema.Schema
import kotlinx.android.synthetic.main.fragment_create_qr_code_location.*

class CreateQrCodeLocationFragment : BaseCreateBarcodeFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScrollView()
        initLatitudeEditText()
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
       return Geo(
           latitude = edit_text_latitude.textString,
           longitude = edit_text_longitude.textString,
           altitude = edit_text_altitude.textString
       )
    }

    private fun initScrollView() {
        scroll_view.makeSmoothScrollable()
    }

    private fun initLatitudeEditText() {
        edit_text_latitude.requestFocus()
    }

    private fun handleTextChanged() {
        edit_text_latitude.addTextChangedListener { toggleCreateBarcodeButton() }
        edit_text_longitude.addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled = edit_text_latitude.isNotBlank() && edit_text_longitude.isNotBlank()
    }
}