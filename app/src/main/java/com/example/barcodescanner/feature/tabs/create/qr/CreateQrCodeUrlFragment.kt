package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.makeSmoothScrollable
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.feature.tabs.create.CreateBarcodeActivity
import kotlinx.android.synthetic.main.fragment_create_qr_code_url.*

class CreateQrCodeUrlFragment : BaseCreateBarcodeFragment() {
    private val parentActivity by lazy { requireActivity() as CreateBarcodeActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_url, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScrollView()
        showUrlPrefix()
        handleTextChanged()
    }

    override fun getBarcodeText(): String {
        return edit_text.text?.toString().orEmpty()
    }

    private fun initScrollView() {
        scroll_view.makeSmoothScrollable()
    }

    private fun showUrlPrefix() {
        val prefix = "https://"
        edit_text.apply {
            setText(prefix)
            setSelection(prefix.length)
            requestFocus()
        }
    }

    private fun handleTextChanged() {
        edit_text.addTextChangedListener {
            if (edit_text.text.isNullOrEmpty()) {
                parentActivity.disableCreateBarcodeButton()
            } else {
                parentActivity.enableCreateBarcodeButton()
            }
        }
    }
}