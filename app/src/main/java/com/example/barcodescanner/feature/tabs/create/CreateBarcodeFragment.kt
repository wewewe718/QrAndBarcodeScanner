package com.example.barcodescanner.feature.tabs.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.BaseActivity
import kotlinx.android.synthetic.main.fragment_create_barcode.*
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
    }

    private fun initScrollView() {
        OverScrollDecoratorHelper.setUpOverScroll(scroll_view)
    }
}