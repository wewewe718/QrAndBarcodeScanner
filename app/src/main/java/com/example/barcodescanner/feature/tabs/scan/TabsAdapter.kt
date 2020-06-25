package com.example.barcodescanner.feature.tabs.scan

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.tabs.scan.camera.ScanBarcodeFromCameraFragment
import com.example.barcodescanner.feature.tabs.scan.file.ScanBarcodeFromFileFragment

class TabsAdapter(context: Context, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private val pageTitles = context.resources.getStringArray(R.array.fragment_scan_barcode_tab_titles)

    override fun getCount(): Int {
        return pageTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ScanBarcodeFromCameraFragment()
            1 -> ScanBarcodeFromFileFragment()
            else -> throw IllegalArgumentException("No fragment for position greater than 1")
        }
    }
}