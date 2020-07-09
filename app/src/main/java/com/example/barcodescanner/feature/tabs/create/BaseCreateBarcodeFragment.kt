package com.example.barcodescanner.feature.tabs.create

import androidx.fragment.app.Fragment
import com.example.barcodescanner.model.schema.Schema

abstract class BaseCreateBarcodeFragment : Fragment() {
    protected val parentActivity by lazy { requireActivity() as CreateBarcodeActivity }

    abstract fun getBarcodeSchema(): Schema
    open fun showPhone(phone: String) {}
}