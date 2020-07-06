package com.example.barcodescanner.feature.tabs.create

import androidx.fragment.app.Fragment

abstract class BaseCreateBarcodeFragment : Fragment() {
    abstract fun getBarcodeText(): String
}