package com.example.barcodescanner.feature.tabs.scan.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import kotlinx.android.synthetic.main.fragment_scan_barcode_from_file.*

class ScanBarcodeFromFileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_barcode_from_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleChooseFileButtonClicked()
    }

    private fun handleChooseFileButtonClicked() {
        button_choose_file.setOnClickListener {
            chooseFile()
        }
    }

    private fun chooseFile() {
        ScanBarcodeFromFileActivity.start(requireActivity())
    }
}