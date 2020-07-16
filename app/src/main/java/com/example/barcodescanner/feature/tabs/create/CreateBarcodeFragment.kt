package com.example.barcodescanner.feature.tabs.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.clipboardManager
import com.example.barcodescanner.extension.makeSmoothScrollable
import com.example.barcodescanner.extension.orZero
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_create_barcode.*
import kotlinx.android.synthetic.main.fragment_create_barcode.scroll_view

class CreateBarcodeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? BaseActivity)?.setWhiteStatusBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScrollView()
        handleButtonsClicked()
    }

    private fun initScrollView() {
        scroll_view.makeSmoothScrollable()
    }

    private fun handleButtonsClicked() {
        // QR code
        button_clipboard.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER, getClipboardContent())  }
        button_text.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER) }
        button_url.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.URL) }
        button_wifi.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.WIFI) }
        button_location.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.GEO) }
        button_contact_vcard.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.VCARD) }
        button_contact_mecard.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.MECARD) }
        button_event.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.VEVENT) }
        button_phone.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.PHONE) }
        button_email.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.EMAIL) }
        button_sms.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.SMS) }
        button_mms.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.MMS) }
        button_cryptocurrency.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.CRYPTOCURRENCY) }
        button_bookmark.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.BOOKMARK) }
        button_app.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.GOOGLE_PLAY) }

        // Barcode
        button_data_matrix.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.DATA_MATRIX) }
        button_aztec.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.AZTEC) }
        button_pdf_417.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.PDF_417) }
        button_codabar.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.CODABAR) }
        button_code_39.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.CODE_39) }
        button_code_93.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.CODE_93) }
        button_code_128.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.CODE_128) }
        button_ean_8.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.EAN_8) }
        button_ean_13.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.EAN_13) }
        button_itf_14.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.ITF) }
        button_upc_a.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.UPC_A) }
        button_upc_e.setOnClickListener { CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.UPC_E) }
    }

    private fun getClipboardContent(): String {
        val clip = requireActivity().clipboardManager?.primaryClip ?: return ""
        return when (clip.itemCount.orZero()) {
            0 -> ""
            else -> clip.getItemAt(0).text.toString()
        }
    }
}