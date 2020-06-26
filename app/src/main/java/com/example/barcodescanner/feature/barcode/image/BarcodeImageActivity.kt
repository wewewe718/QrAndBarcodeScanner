package com.example.barcodescanner.feature.barcode.image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeImageGenerator
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.common.toStringId
import com.example.barcodescanner.model.Barcode
import kotlinx.android.synthetic.main.activity_barcode_image.*
import java.text.SimpleDateFormat
import java.util.*

class BarcodeImageActivity : BaseActivity() {

    companion object {
        private const val BARCODE_KEY = "BARCODE_KEY"

        fun start(context: Context, barcode: Barcode) {
            val intent = Intent(context, BarcodeImageActivity::class.java)
            intent.putExtra(BARCODE_KEY, barcode)
            context.startActivity(intent)
        }
    }

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    private val barcode by lazy {
        intent?.getParcelableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_image)
        handleToolbarBackPressed()
        showBarcode()
    }

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showBarcode() {
        showBarcodeImage()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
    }

    private fun showBarcodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateImage(barcode, 2000, 2000, 0)
            image_view_barcode.setImageBitmap(bitmap)
        } catch (ex: Exception) {
            image_view_barcode.isVisible = false
            ex.printStackTrace()
        }
    }

    private fun showBarcodeDate() {
        text_view_date.text = dateFormatter.format(barcode.date)
    }

    private fun showBarcodeFormat() {
        val format = barcode.format.toStringId()
        toolbar.setTitle(format)
    }

    private fun showBarcodeText() {
        text_view_barcode_text.text = barcode.text
    }
}