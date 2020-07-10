package com.example.barcodescanner.feature.tabs.create

import androidx.fragment.app.Fragment
import com.example.barcodescanner.model.schema.Schema

abstract class BaseCreateBarcodeFragment : Fragment() {
    protected val parentActivity by lazy { requireActivity() as CreateBarcodeActivity }

    open val latitude: Double? = null
    open val longitude: Double? = null

    abstract fun getBarcodeSchema(): Schema
    open fun showPhone(phone: String) {}
    open fun showLocation(latitude: Double?, longitude: Double?) {}
}