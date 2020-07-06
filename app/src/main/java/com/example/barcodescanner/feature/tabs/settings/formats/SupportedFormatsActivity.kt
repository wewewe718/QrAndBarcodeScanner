package com.example.barcodescanner.feature.tabs.settings.formats

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barcodescanner.R
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.makeSmoothScrollable
import com.example.barcodescanner.feature.BaseActivity
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.activity_supported_formats.*

class SupportedFormatsActivity : BaseActivity(), FormatsAdapter.Listener {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SupportedFormatsActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val formats by lazy { BarcodeFormat.values() }
    private val formatSelection by lazy { formats.map(settings::isFormatSelected) }
    private val formatsAdapter by lazy { FormatsAdapter(this, formats, formatSelection) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supported_formats)
        initRecyclerView()
        handleToolbarBackClicked()
    }

    override fun onFormatChecked(format: BarcodeFormat, isChecked: Boolean) {
        settings.setFormatSelected(format, isChecked)
    }

    private fun initRecyclerView() {
        recycler_view_formats.apply {
            layoutManager = LinearLayoutManager(this@SupportedFormatsActivity)
            adapter = formatsAdapter
            makeSmoothScrollable()
        }
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}