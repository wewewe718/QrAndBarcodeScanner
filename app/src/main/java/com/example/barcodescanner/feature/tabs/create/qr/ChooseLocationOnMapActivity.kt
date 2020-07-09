package com.example.barcodescanner.feature.tabs.create.qr

import android.os.Bundle
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.BaseActivity
import kotlinx.android.synthetic.main.activity_choose_location_on_map.*

class ChooseLocationOnMapActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_location_on_map)
        handleToolbarBackClicked()
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}