package com.example.barcodescanner.feature.tabs.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.extension.clipboardManager
import com.example.barcodescanner.extension.orZero
import com.example.barcodescanner.feature.tabs.create.barcode.CreateBarcodeAllActivity
import com.example.barcodescanner.feature.tabs.create.qr.CreateQrCodeAllActivity
import com.example.barcodescanner.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_create_barcode.*

class CreateBarcodeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportEdgeToEdge()
        handleButtonsClicked()
    }

    private fun supportEdgeToEdge() {
        app_bar_layout.applySystemWindowInsets(applyTop = true)
    }

    private fun handleButtonsClicked() {
        // QR code
        button_clipboard.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER, getClipboardContent())  }
        button_text.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER) }
        button_url.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.URL) }
        button_wifi.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.WIFI) }
        button_location.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.GEO) }
        button_contact_vcard.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.VCARD) }
        button_show_all_qr_code.setOnClickListener { CreateQrCodeAllActivity.start(requireActivity()) }

        // Barcode
        button_create_barcode.setOnClickListener { CreateBarcodeAllActivity.start(requireActivity()) }
    }

    private fun getClipboardContent(): String {
        val clip = requireActivity().clipboardManager?.primaryClip ?: return ""
        return when (clip.itemCount.orZero()) {
            0 -> ""
            else -> clip.getItemAt(0).text.toString()
        }
    }
}