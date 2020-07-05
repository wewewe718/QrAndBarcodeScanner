package com.example.barcodescanner.feature.tabs.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.android.synthetic.main.fragment_create_barcode.*
import kotlinx.android.synthetic.main.fragment_create_barcode.scroll_view
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

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
        OverScrollDecoratorHelper.setUpOverScroll(scroll_view)
    }

    private fun handleButtonsClicked() {
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
    }
}