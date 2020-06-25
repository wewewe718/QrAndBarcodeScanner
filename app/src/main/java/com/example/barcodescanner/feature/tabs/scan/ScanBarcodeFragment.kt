package com.example.barcodescanner.feature.tabs.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import kotlinx.android.synthetic.main.fragment_scan_barcode.*

class ScanBarcodeFragment : Fragment() {
    private val pagerAdapter by lazy {
        TabsAdapter(requireContext(), childFragmentManager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabs()
    }

    private fun initTabs() {
        view_pager.adapter = pagerAdapter
        tab_layout.setupWithViewPager(view_pager)
    }


}